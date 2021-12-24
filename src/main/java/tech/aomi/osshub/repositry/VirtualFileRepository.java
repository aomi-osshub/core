package tech.aomi.osshub.repositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.aomi.osshub.entity.VirtualFile;

import java.util.List;
import java.util.Optional;

/**
 * @author Sean createAt 2021/10/22
 */
@Repository
public interface VirtualFileRepository extends MongoRepository<VirtualFile, String> {

    /**
     * 客户端对应的文件是否存在
     *
     * @param type      文件类型
     * @param clientId  客户端ID
     * @param userId    用户ID
     * @param directory 所在目录
     * @param name      文件名称
     * @return 是否存在
     */
    boolean existsByTypeAndClientIdAndUserIdAndDirectoryAndName(VirtualFile.Type type, String clientId, String userId, String directory, String name);

    Optional<VirtualFile> findByClientIdAndId(String clientId, String id);

    Optional<VirtualFile> findByClientIdAndDirectoryAndName(String clientId, String directory, String name);

    List<VirtualFile> findByClientIdAndDirectory(String clientId, String directory);

    /**
     * 查询指定目录下的所有子文件
     *
     * @param clientId  客户端ID
     * @param directory 当前目录
     * @return 所有子目录
     */
    List<VirtualFile> findByClientIdAndDirectoryIsStartingWith(String clientId, String directory);
}
