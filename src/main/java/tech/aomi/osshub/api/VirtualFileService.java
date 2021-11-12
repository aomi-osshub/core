package tech.aomi.osshub.api;

import tech.aomi.osshub.entity.Client;
import tech.aomi.osshub.entity.VirtualFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @author Sean createAt 2021/10/21
 */
public interface VirtualFileService {


    /**
     * 访问文件
     *
     * @param id 文件id
     */
    Optional<VirtualFile> visit(String id);

    List<VirtualFile> query(String clientId, String directory);

    /**
     * 文件夹创建
     *
     * @param client 客户端
     * @param parent 上级目录
     * @param name   文件夹名称
     * @return 文件夹
     */
    VirtualFile createDirectory(Client client, String userId, String parent, String name);

    /**
     * 文件保存
     *
     * @param virtualFile     文件信息
     * @param fileInputStream 文件流信息
     * @return 文件信息
     */
    VirtualFile save(Client client, VirtualFile virtualFile, InputStream fileInputStream);


}
