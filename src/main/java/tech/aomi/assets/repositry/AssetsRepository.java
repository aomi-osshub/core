package tech.aomi.assets.repositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.aomi.assets.entity.Assets;

/**
 * @author Sean Create At 2020/4/25
 */
@Repository
public interface AssetsRepository extends MongoRepository<Assets, String> {

}
