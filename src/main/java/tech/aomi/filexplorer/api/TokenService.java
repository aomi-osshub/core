package tech.aomi.filexplorer.api;

import tech.aomi.filexplorer.entity.Token;

import java.util.Optional;

/**
 * @author Sean createAt 2021/10/26
 */
public interface TokenService {

    /**
     * 根据ID查找token
     *
     * @param tokenId token id
     * @return token 信息
     */
    Optional<Token> findById(String tokenId);

    /**
     * 查询有效的token
     *
     * @param tokenId token id
     * @return token信息
     */
    Optional<Token> findValidById(String tokenId);

    /**
     * 创建TOKEN
     *
     * @param token token 基本信息
     * @return token 信息
     */
    Token create(Token token);

}
