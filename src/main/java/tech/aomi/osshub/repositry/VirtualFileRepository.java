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
     * @param type     文件类型
     * @param clientId 客户端ID
     * @param userId   用户ID
     * @param name     文件名称
     * @return 是否存在
     */
    boolean existsByTypeAndClientIdAndUserIdAndName(VirtualFile.Type type, String clientId, String userId, String name);

    Optional<VirtualFile> findByClientIdAndId(String clientId, String id);

    List<VirtualFile> findByClientIdAndDirectory(String clientId, String directory);
}
