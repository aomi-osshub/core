package tech.aomi.filexplorer.repositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.aomi.filexplorer.entity.Assets;

/**
 * @author Sean Create At 2020/4/25
 */
@Repository
public interface AssetsRepository extends MongoRepository<Assets, String> {

}
