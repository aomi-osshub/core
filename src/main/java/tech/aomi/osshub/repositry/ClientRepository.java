package tech.aomi.osshub.repositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.aomi.osshub.entity.Client;

/**
 * @author Sean createAt 2021/10/22
 */
@Repository
public interface ClientRepository extends MongoRepository<Client, String> {

}
