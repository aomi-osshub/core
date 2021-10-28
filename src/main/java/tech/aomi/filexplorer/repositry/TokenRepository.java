package tech.aomi.filexplorer.repositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.aomi.filexplorer.entity.Token;

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
