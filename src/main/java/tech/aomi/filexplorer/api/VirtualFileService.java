package tech.aomi.filexplorer.api;

import tech.aomi.filexplorer.entity.Client;
import tech.aomi.filexplorer.entity.VirtualFile;

import java.io.InputStream;

/**
 * @author Sean createAt 2021/10/21
 */
public interface VirtualFileService {

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
