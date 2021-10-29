package tech.aomi.osshub.repositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.aomi.osshub.entity.Token;

import java.util.Date;
import java.util.Optional;

/**
 * @author Sean createAt 2021/10/26
 */
@Repository
public interface TokenRepository extends MongoRepository<Token, String> {

    boolean existsByRequestId(String requestId);

    Optional<Token> findByIdAndExpirationAtGreaterThan(String tokenId, Date now);
}
