package tech.aomi.osshub.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.aomi.common.exception.ServiceException;
import tech.aomi.common.utils.MapBuilder;
import tech.aomi.osshub.CoreProperties;
import tech.aomi.osshub.api.VirtualFileService;
import tech.aomi.osshub.common.exception.*;
import tech.aomi.osshub.entity.Client;
import tech.aomi.osshub.entity.FileSystemStorageParams;
import tech.aomi.osshub.entity.StorageType;
import tech.aomi.osshub.entity.VirtualFile;
import tech.aomi.osshub.repositry.VirtualFileRepository;
import tech.aomi.spring.data.mongo.repository.QueryBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    public VirtualFile createDirectory(Client client, String userId, String parentDir, String name) {
        Assert.notNull(client, "??????????????????NULL");
        Assert.hasLength(name, "?????????????????????");
        LOGGER.debug("?????????: {}", parentDir);

        String directory = StringUtils.trimToEmpty(parentDir);
        String pId;
        if ("".equals(directory) || "/".equals(directory)) {
            directory = "/";
            pId = "";
        } else {
            Path parentPath = Paths.get(directory);
            VirtualFile parent = virtualFileRepository.findByClientIdAndDirectoryAndName(
                    client.getId(),
                    parentPath.getParent().toString(),
                    parentPath.getFileName().toString()
            ).orElseThrow(() -> new FileNonExistException("??????????????????"));
            pId = parent.getId();
        }

        if (virtualFileRepository.existsByTypeAndClientIdAndUserIdAndDirectoryAndName(
                VirtualFile.Type.DIRECTORY,
                client.getId(),
                userId,
                directory,
                name
        )) {
            throw new DirExistException("??????????????????: " + name);
        }


        StorageType storageType = client.getStorageType();
        if (null == storageType) {
            storageType = StorageType.FILE_SYSTEM;
        }

        VirtualFile virtualFile = new VirtualFile();
        virtualFile.setType(VirtualFile.Type.DIRECTORY);
        virtualFile.setDirectory(directory);
        virtualFile.setDirectoryId(pId);
        virtualFile.setName(name);
        virtualFile.setClientId(client.getId());
        virtualFile.setUserId(userId);
        virtualFile.setStorageType(storageType);
        virtualFile.setCreateAt(new Date());
        return virtualFileRepository.save(virtualFile);
    }


    @Override
    public VirtualFile save(Client client, VirtualFile virtualFile, InputStream fileInputStream) {
        Assert.notNull(virtualFile, "?????????????????????NULL");
        Assert.notNull(fileInputStream, "?????????????????????NULL");

        Assert.hasLength(virtualFile.getUserId(), "??????ID????????????");
        Assert.hasLength(virtualFile.getName(), "?????????????????????");

        String directory = StringUtils.trimToEmpty(virtualFile.getDirectory());
        String pId;
        if ("".equals(directory) || "/".equals(directory)) {
            directory = "/";
            pId = "";
        } else {
            Path parentPath = Paths.get(directory);
            VirtualFile parent = virtualFileRepository.findByClientIdAndDirectoryAndName(
                    client.getId(),
                    parentPath.getParent().toString(),
                    parentPath.getFileName().toString()
            ).orElseThrow(() -> new FileNonExistException("??????????????????"));

            pId = parent.getId();
        }

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
        virtualFile.setDirectoryId(pId);
        virtualFile.setAccessSource(virtualFile.getId() + "/" + virtualFile.getName());

        switch (storageType) {
            case FILE_SYSTEM:
                saveWithFileSystem(virtualFile, fileInputStream, client);
                break;
            default:
                throw new ServiceException("????????????????????????: " + storageType);
        }
        return virtualFileRepository.save(virtualFile);
    }

    @Override
    public void move(Client client, List<String> sourceIds, String targetDir) {
        File file = new File(targetDir);

        VirtualFile targetFile = virtualFileRepository.findByClientIdAndDirectoryAndName(client.getId(), file.getParent(), file.getName()).orElseThrow(() -> new FileNonExistException("????????????????????????"));
        if (VirtualFile.Type.DIRECTORY != targetFile.getType()) {
            LOGGER.error("??????????????????????????????: {}", targetDir);
            throw new FileNonExistException("????????????????????????");
        }

        Query dirQuery = QueryBuilder.builder()
                .is("clientId", client.getId())
                .in("id", sourceIds)
                .is("type", VirtualFile.Type.DIRECTORY)
                .build();

        List<VirtualFile> dirs = mongoTemplate.find(dirQuery, VirtualFile.class);
        dirs.parallelStream().forEach(item -> {
            Path dirPath = item.getFullPath();
            String dir = dirPath.toString();
            LOGGER.debug("????????????????????????????????????: source={} target={}", dir, targetDir);

            List<Document> u = new ArrayList<>();
            u.add(new Document(MapBuilder.of(
                    "$set", MapBuilder.of(
                            "directory", MapBuilder.of(
                                    "$replaceOne", MapBuilder.of(
                                            "input", "$directory",
                                            "find", dirPath.getParent().toString(),
                                            "replacement", targetDir
                                    )
                            )
                    )
            )));

            Document command = new Document();
            command.append("update", mongoTemplate.getCollectionName(VirtualFile.class));
            Map<String, Object> update = MapBuilder.of(
                    "q", MapBuilder.of(
                            "clientId", client.getId(),
                            "directory", MapBuilder.of(
                                    "$regex", dir + ".*"
                            )
                    ),
                    "multi", true,
                    "u", u
            );
            List<Document> updates = new ArrayList<>();
            updates.add(new Document(update));
            command.append("updates", updates);
            LOGGER.debug("????????????????????????: {}", command.toJson());
            mongoTemplate.executeCommand(command);
        });

        LOGGER.debug("???????????????????????????????????????: {}", targetDir);
        Query fileQuery = QueryBuilder.builder()
                .is("clientId", client.getId())
                .in("id", sourceIds)
                .build();

        mongoTemplate.updateMulti(
                fileQuery,
                new Update()
                        .set("directory", targetDir)
                        .set("directoryId", targetFile.getId())
                ,
                VirtualFile.class
        );
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

            LOGGER.debug("????????????: {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("????????????????????? {}", e.getMessage(), e);
            throw new FileCreateException("??????????????????", e);
        }

    }

    private void del(Client client, VirtualFile file) {
        File clientDir = getClientFile(client);
        if (file.getType() == VirtualFile.Type.FILE) {
            File fileDir = getFileDir(clientDir, file);
            try {
                FileUtils.deleteDirectory(fileDir);
                virtualFileRepository.deleteById(file.getId());
            } catch (IOException e) {
                LOGGER.warn("??????????????????: id={} name={} message={}", file.getId(), file.getName(), e.getMessage());
            }
            return;
        }

        List<VirtualFile> allSubFiles = virtualFileRepository.findByClientIdAndDirectoryIsStartingWith(
                client.getId(),
                file.getFullName()
        );

        allSubFiles.parallelStream()
                .forEach(item -> {
                    File fileDir = getFileDir(clientDir, file);
                    try {
                        FileUtils.deleteDirectory(fileDir);
                    } catch (Exception e) {
                        LOGGER.warn("??????????????????: id={} name={}", file.getId(), file.getName(), e);
                    }
                });

        virtualFileRepository.deleteAll(allSubFiles);
        virtualFileRepository.deleteById(file.getId());
    }


    private File getRootFile() {
        String rootDirPath = properties.getRootDir();
        LOGGER.debug("?????????: {}", rootDirPath);
        File rootDir = new File(rootDirPath);
        if (!rootDir.exists()) {
            LOGGER.error("??????????????????: {}", rootDirPath);
            throw new DirNonExistException("??????????????????");
        }
        return rootDir;
    }


    private File getClientFile(Client client) {
        File rootDir = getRootFile();

        // ???????????????????????????????????????????????????????????????????????????
        String clientPath = (String) client.getLabel(FileSystemStorageParams.ROOT_DIR_KEY);
        if (null == clientPath) {
            throw new ServiceException("??????????????????????????????");
        }

        String clientDirPath = rootDir.getAbsolutePath() + File.separator + clientPath;
        File clientDir = new File(clientDirPath);
        if (!clientDir.exists() && !clientDir.mkdir()) {
            LOGGER.error("?????????????????????????????????: {}", clientDirPath);
            throw new DirCreateException("?????????????????????????????????:" + clientPath);
        }
        return clientDir;
    }

    private File getFileDir(Client client, VirtualFile virtualFile) {
        File clientDir = getClientFile(client);
        return getFileDir(clientDir, virtualFile);
    }

    private File getFileDir(File clientDir, VirtualFile virtualFile) {
        String fileDirPath = clientDir.getAbsolutePath() + File.separator + virtualFile.getId();
        LOGGER.debug("????????????: {}", fileDirPath);
        File fileDir = new File(fileDirPath);
        if (!fileDir.exists() && !fileDir.mkdir()) {
            LOGGER.error("????????????????????????: {}", fileDirPath);
            throw new DirCreateException("????????????????????????");
        }
        return fileDir;
    }

}
