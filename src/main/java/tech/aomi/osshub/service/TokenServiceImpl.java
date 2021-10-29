package tech.aomi.osshub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.aomi.common.exception.ResourceExistException;
import tech.aomi.osshub.api.TokenService;
import tech.aomi.osshub.common.exception.ClientNonExistException;
import tech.aomi.osshub.entity.Client;
import tech.aomi.osshub.entity.Token;
import tech.aomi.osshub.repositry.ClientRepository;
import tech.aomi.osshub.repositry.TokenRepository;

import java.util.Date;
import java.util.Optional;

/**
 * token 服务实现
 *
 * @author Sean createAt 2021/10/26
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Optional<Token> findById(String tokenId) {
        return tokenRepository.findById(tokenId);
    }

    @Override
    public Optional<Token> findValidById(String tokenId) {
        return tokenRepository.findByIdAndExpirationAtGreaterThan(tokenId, new Date());
    }

    @Override
    public Token create(Token token) {
        Assert.notNull(token, "token 不能为 null");
        Assert.hasLength(token.getClientId(), "client id 不能为空");
        Assert.hasLength(token.getRequestId(), "请求id不能为空");

        if (tokenRepository.existsByRequestId(token.getRequestId())) {
            LOGGER.error("无效的request id: {}", token.getRequestId());
            throw new ResourceExistException("RequestId已经存在");
        }

        Client client = clientRepository.findById(token.getClientId()).orElseThrow(() -> new ClientNonExistException("客户端不存在"));
        token.setClient(client);
        token.setCreateAt(new Date());

        return tokenRepository.save(token);
    }
}
