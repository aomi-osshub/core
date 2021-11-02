package tech.aomi.osshub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
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
import tech.aomi.osshub.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @Override
    public Optional<VirtualFile> findById(String id) {
        return virtualFileRepository.findById(id);
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

        if (!"/".equals(parentDirectory) && !virtualFileRepository.existsByTypeAndClientIdAndUserIdAndName(
                VirtualFile.Type.DIRECTORY,
                client.getId(),
                userId,
                FileUtil.getName(parentDirectory)

        )) {
            throw new DirNonExistException("父目录不存在:" + parentDirectory);
        }

        if (virtualFileRepository.existsByTypeAndClientIdAndUserIdAndName(
                VirtualFile.Type.DIRECTORY,
                client.getId(),
                userId,
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

        switch (storageType) {
            case FILE_SYSTEM:
                saveDirectoryFileSystem(virtualFile, client);
                break;
            default:
                throw new ServiceException("不支持的存储方式: " + storageType);
        }
        virtualFile.setFullName(parentDirectory + ("/".equals(parentDirectory) ? "" : File.separator) + name);
        return virtualFileRepository.save(virtualFile);
    }


    @Override
    public VirtualFile save(Client client, VirtualFile virtualFile, InputStream fileInputStream) {
        Assert.notNull(virtualFile, "文件信息不能为NULL");
        Assert.notNull(fileInputStream, "文件信息不能为NULL");

        Assert.hasLength(virtualFile.getUserId(), "用户ID不能为空");
        Assert.hasLength(virtualFile.getName(), "文件名不能为空");

        StorageType storageType = client.getStorageType();
        if (null == storageType) {
            storageType = StorageType.FILE_SYSTEM;
        }
        virtualFile.setId(new ObjectId().toString());
        virtualFile.setType(VirtualFile.Type.FILE);
        virtualFile.setCreateAt(new Date());
        virtualFile.setStorageType(storageType);
        virtualFile.setClientId(client.getId());

        switch (storageType) {
            case FILE_SYSTEM:
                saveFileSystem(virtualFile, fileInputStream, client);
                break;
            default:
                throw new ServiceException("不支持的存储方式: " + storageType);
        }
        virtualFile.setFullName(virtualFile.getDirectory() + ("/".equals(virtualFile.getDirectory()) ? "" : File.separator) + virtualFile.getName());
        return virtualFileRepository.save(virtualFile);
    }

    private void saveDirectoryFileSystem(VirtualFile virtualFile, Client client) {
        File userRoot = userRootDir(client, virtualFile.getUserId());

        String parentDirPath = userRoot.getAbsolutePath() + (virtualFile.getDirectory().startsWith("/") ? "" : File.separator) + virtualFile.getDirectory();
        File parentDirFile = new File(parentDirPath);
        if (!parentDirFile.exists() || parentDirFile.isFile()) {
            boolean parentCreate = parentDirFile.mkdirs();
            LOGGER.debug("父目录不存在，自动创建: {}, {}", parentDirPath, parentCreate);
        }

        String dirPath = parentDirPath + File.separator + virtualFile.getName();
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            LOGGER.error("目录已经存在: {}", dirPath);
            throw new DirExistException("目录已经存在:" + virtualFile.getName());
        }

        if (!dir.mkdir()) {
            throw new DirCreateException("目录创建失败: " + virtualFile.getName());
        }
    }

    private void saveFileSystem(VirtualFile virtualFile, InputStream fileInputStream, Client client) {

        File userRoot = userRootDir(client, virtualFile.getUserId());

        String directory = StringUtils.trimToEmpty(virtualFile.getDirectory());
        if ("".equals(directory)) {
            directory = "/";
        }
        virtualFile.setDirectory(directory);

        String fileDirPath = userRoot.getAbsolutePath() + (directory.startsWith("/") ? "" : File.separator) + directory;
        LOGGER.debug("文件目录: {}", fileDirPath);
        File fileDir = new File(fileDirPath);
        if (!fileDir.exists() || fileDir.isFile()) {
            LOGGER.error("文件上传指定的目录不存在: {}", fileDirPath);
            throw new DirNonExistException("文件上传的目录不存在");
        }

        int index = virtualFile.getName().lastIndexOf(".");
        String toPrefix = "";
        String toSuffix = "";
        if (index == -1) {
            toPrefix = virtualFile.getName();
        } else {
            toPrefix = virtualFile.getName().substring(0, index);
            toSuffix = virtualFile.getName().substring(index);
        }

        File file = new File(fileDir, toPrefix + toSuffix);
        for (int i = 1; file.exists() && i < Integer.MAX_VALUE; i++) {
            file = new File(fileDir, toPrefix + '(' + i + ')' + toSuffix);
        }
        virtualFile.setName(file.getName());
        try {
            FileUtils.copyInputStreamToFile(fileInputStream, file);

            File root = new File(properties.getRootDir());
            String rootAbsolutePath = root.getAbsolutePath();
            String fileAbsolutePath = file.getAbsolutePath();

            String storageSource = fileAbsolutePath.replace(rootAbsolutePath, "");
            virtualFile.setStorageSource(storageSource);

            LOGGER.debug("文件路径: {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("文件保存失败： {}", e.getMessage(), e);
            throw new FileCreateException("文件创建失败", e);
        }

    }

    private File userRootDir(Client client, String userId) {
        Map<String, Object> params = client.getStorageParams();
        if (CollectionUtils.isEmpty(params) || StringUtils.isEmpty((CharSequence) params.get(FileSystemStorageParams.ROOT_DIR_KEY))) {
            throw new ServiceException("客户端未配置存储路径信息");
        }
        String clientRootDir = (String) params.get(FileSystemStorageParams.ROOT_DIR_KEY);
        String rootDirPath = properties.getRootDir() + File.separator + clientRootDir;
        LOGGER.debug("客户端根目录: {}", rootDirPath);
        File rootDir = new File(rootDirPath);
        if (!rootDir.exists() && !rootDir.mkdirs()) {
            LOGGER.error("客户端根目录创建失败: {}", rootDirPath);
            throw new DirCreateException("客户端根目录创建失败:" + File.separator + clientRootDir);
        } else if (rootDir.isFile() && !rootDir.mkdirs()) {
            LOGGER.error("客户端根目录创建失败: {}", rootDirPath);
            throw new DirCreateException("客户端根目录创建失败:" + File.separator + clientRootDir);
        }

        String userDirPath = rootDirPath + File.separator + userId;
        LOGGER.debug("用户目录: {}", userDirPath);
        File userDir = new File(userDirPath);
        if (!userDir.exists() && !userDir.mkdirs()) {
            LOGGER.error("用户录创建失败: {}", rootDirPath);
            throw new DirCreateException("用户录创建失败:" + File.separator + userDirPath);
        } else if (userDir.isFile() && !userDir.mkdirs()) {
            LOGGER.error("用户录创建失败: {}", rootDirPath);
            throw new DirCreateException("用户录创建失败:" + File.separator + userDirPath);
        }

        return userDir;
    }
}
