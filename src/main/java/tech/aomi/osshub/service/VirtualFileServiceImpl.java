package tech.aomi.osshub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.aomi.common.exception.ServiceException;
import tech.aomi.osshub.CoreProperties;
import tech.aomi.osshub.api.VirtualFileService;
import tech.aomi.osshub.common.exception.DirCreateException;
import tech.aomi.osshub.common.exception.DirExistException;
import tech.aomi.osshub.common.exception.DirNonExistException;
import tech.aomi.osshub.common.exception.FileCreateException;
import tech.aomi.osshub.entity.Client;
import tech.aomi.osshub.entity.FileSystemStorageParams;
import tech.aomi.osshub.entity.StorageType;
import tech.aomi.osshub.entity.VirtualFile;
import tech.aomi.osshub.repositry.VirtualFileRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Sean createAt 2021/10/21
 */
@Slf4j
@Service
public class VirtualFileServiceImpl implements VirtualFileService {

    @Autowired
    private CoreProperties properties;

    @Autowired
    private VirtualFileRepository virtualFileRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Optional<VirtualFile> visit(String id) {
        VirtualFile file = mongoTemplate.findAndModify(
                new Query(Criteria.where("id").is(id)),
                new Update().inc("visits", 1L)
                        .set("lastVisitAt", new Date()),
                VirtualFile.class
        );
        return Optional.ofNullable(file);
    }

    @Override
    public List<VirtualFile> query(String clientId, String directory) {
        return virtualFileRepository.findByClientIdAndDirectory(clientId, directory);
    }

    @Override
    public VirtualFile createDirectory(Client client, String userId, String parent, String name) {
        Assert.notNull(client, "客户端不能为NULL");
        Assert.hasLength(name, "目录名不能为空");
        LOGGER.debug("父目录: {}", parent);

        StorageType storageType = client.getStorageType();
        if (null == storageType) {
            storageType = StorageType.FILE_SYSTEM;
        }
        parent = StringUtils.trimToEmpty(parent);
        String parentDirectory = "/";
        if (StringUtils.isNotEmpty(parent)) {
            parentDirectory = parent;
        }

        Path parentPath = Paths.get(parentDirectory);

        if (!"/".equals(parentDirectory) && !virtualFileRepository.existsByTypeAndClientIdAndUserIdAndDirectoryAndName(
                VirtualFile.Type.DIRECTORY,
                client.getId(),
                userId,
                parentPath.getParent().toString(),
                parentPath.getFileName().toString()

        )) {
            throw new DirNonExistException("父目录不存在:" + parentDirectory);
        }

        if (virtualFileRepository.existsByTypeAndClientIdAndUserIdAndDirectoryAndName(
                VirtualFile.Type.DIRECTORY,
                client.getId(),
                userId,
                parentDirectory,
                name
        )) {
            throw new DirExistException("目录已经存在: " + name);
        }

        VirtualFile virtualFile = new VirtualFile();
        virtualFile.setType(VirtualFile.Type.DIRECTORY);
        virtualFile.setDirectory(parentDirectory);
        virtualFile.setName(name);
        virtualFile.setClientId(client.getId());
        virtualFile.setUserId(userId);
        virtualFile.setStorageType(storageType);
        virtualFile.setFullName(parentDirectory + ("/".equals(parentDirectory) ? "" : File.separator) + name);
        return virtualFileRepository.save(virtualFile);
    }


    @Override
    public VirtualFile save(Client client, VirtualFile virtualFile, InputStream fileInputStream) {
        Assert.notNull(virtualFile, "文件信息不能为NULL");
        Assert.notNull(fileInputStream, "文件信息不能为NULL");

        Assert.hasLength(virtualFile.getUserId(), "用户ID不能为空");
        Assert.hasLength(virtualFile.getName(), "文件名不能为空");

        String directory = StringUtils.trimToEmpty(virtualFile.getDirectory());
        if ("".equals(directory)) {
            directory = "/";
        }

//        if (virtualFileRepository.existsByTypeAndClientIdAndUserIdAndDirectoryAndName(
//                VirtualFile.Type.FILE,
//                client.getId(),
//                virtualFile.getUserId(),
//                directory,
//                virtualFile.getName()
//        )) {
//            FileExistException e = new FileExistException("文件已经存在: " + virtualFile.getName());
//            e.setPayload(virtualFile.getName());
//            throw e;
//        }

        StorageType storageType = client.getStorageType();
        if (null == storageType) {
            storageType = StorageType.FILE_SYSTEM;
        }
        virtualFile.setId(new ObjectId().toString());
        virtualFile.setType(VirtualFile.Type.FILE);
        virtualFile.setCreateAt(new Date());
        virtualFile.setStorageType(storageType);
        virtualFile.setClientId(client.getId());

        virtualFile.setDirectory(directory);
        virtualFile.setAccessSource(virtualFile.getId() + "/" + virtualFile.getName());

        switch (storageType) {
            case FILE_SYSTEM:
                saveWithFileSystem(virtualFile, fileInputStream, client);
                break;
            default:
                throw new ServiceException("不支持的存储方式: " + storageType);
        }
        virtualFile.setFullName(virtualFile.getDirectory() + ("/".equals(virtualFile.getDirectory()) ? "" : File.separator) + virtualFile.getName());
        return virtualFileRepository.save(virtualFile);
    }

    @Override
    public void del(Client client, List<String> ids) {
        Iterable<VirtualFile> files = virtualFileRepository.findAllById(ids);
        files.forEach(file -> this.del(client, file));
    }

    private void saveWithFileSystem(VirtualFile virtualFile, InputStream fileInputStream, Client client) {
        File rootDir = getRootFile();
        File fileDir = getFileDir(client, virtualFile);
        File file = new File(fileDir, virtualFile.getName());

        try {
            FileUtils.copyInputStreamToFile(fileInputStream, file);

            String rootAbsolutePath = rootDir.getAbsolutePath();
            String fileAbsolutePath = file.getAbsolutePath();

            String storageSource = fileAbsolutePath.replace(rootAbsolutePath, "");
            virtualFile.setStorageSource(storageSource);

            LOGGER.debug("文件路径: {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("文件保存失败： {}", e.getMessage(), e);
            throw new FileCreateException("文件创建失败", e);
        }

    }

    private void del(Client client, VirtualFile file) {
        File clientDir = getClientFile(client);
        if (file.getType() == VirtualFile.Type.FILE) {
            File fileDir = getFileDir(clientDir, file);
            boolean success = fileDir.delete();
            if (success) {
                virtualFileRepository.deleteById(file.getId());
            } else {
                LOGGER.warn("文件删除失败: id={} name={}", file.getId(), file.getName());
            }
            return;
        }

        List<VirtualFile> allSubFiles = virtualFileRepository.findByClientIdAndDirectoryIsStartingWith(
                client.getId(),
                file.getDirectory() + file.getName()
        );

        allSubFiles.parallelStream()
                .forEach(item -> {
                    File fileDir = getFileDir(clientDir, file);
                    boolean success = fileDir.delete();
                    if (!success) {
                        LOGGER.warn("文件删除失败: id={} name={}", file.getId(), file.getName());
                    }
                });

        virtualFileRepository.deleteAll(allSubFiles);
    }


    private File getRootFile() {
        String rootDirPath = properties.getRootDir();
        LOGGER.debug("根目录: {}", rootDirPath);
        File rootDir = new File(rootDirPath);
        if (!rootDir.exists()) {
            LOGGER.error("根目录不存在: {}", rootDirPath);
            throw new DirNonExistException("根目录不存在");
        }
        return rootDir;
    }


    private File getClientFile(Client client) {
        File rootDir = getRootFile();

        // 客户端配置存储路径是为了多个客户端共享一个存储目录
        String clientPath = (String) client.getLabel(FileSystemStorageParams.ROOT_DIR_KEY);
        if (null == clientPath) {
            throw new ServiceException("客户端未配置存储目录");
        }

        String clientDirPath = rootDir.getAbsolutePath() + File.separator + clientPath;
        File clientDir = new File(clientDirPath);
        if (!clientDir.exists() && !clientDir.mkdir()) {
            LOGGER.error("客户端存储目录创建失败: {}", clientDirPath);
            throw new DirCreateException("客户端存储目录创建失败:" + clientPath);
        }
        return clientDir;
    }

    private File getFileDir(Client client, VirtualFile virtualFile) {
        File clientDir = getClientFile(client);
        return getFileDir(clientDir, virtualFile);
    }

    private File getFileDir(File clientDir, VirtualFile virtualFile) {
        String fileDirPath = clientDir.getAbsolutePath() + File.separator + virtualFile.getId();
        LOGGER.debug("文件目录: {}", fileDirPath);
        File fileDir = new File(fileDirPath);
        if (!fileDir.mkdir()) {
            LOGGER.error("文件目录创建失败: {}", fileDirPath);
            throw new DirCreateException("文件目录创建失败");
        }
        return fileDir;
    }

}
