package tech.aomi.filexplorer.repositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.aomi.filexplorer.entity.VirtualFile;

/**
 * @author Sean createAt 2021/10/22
 */
@Repository
public interface VirtualFileRepository extends MongoRepository<VirtualFile, String> {

    /**
     * 客户端对应的文件是否存在
     *
     * @param type     文件类型
     * @param clientId 客户端ID
     * @param userId   用户ID
     * @param name     文件名称
     * @return 是否存在
     */
    boolean existsByTypeAndClientIdAndUserIdAndName(VirtualFile.Type type, String clientId, String userId, String name);

}