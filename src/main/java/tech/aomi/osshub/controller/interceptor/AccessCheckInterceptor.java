package tech.aomi.osshub.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.aomi.osshub.api.TokenService;
import tech.aomi.osshub.constant.HttpHeader;
import tech.aomi.osshub.entity.Token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 访问权限检查拦截器
 *
 * @author Sean createAt 2021/10/26
 */
@Slf4j
@Component
public class AccessCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenId = request.getHeader(HttpHeader.TOKEN);
        LOGGER.debug("操作对应的ID: {}", tokenId);
        Assert.hasLength(tokenId, "TOKEN 信息不能为空");
        Optional<Token> tokenOptional = tokenService.findById(tokenId);
        Token token = tokenOptional.orElseThrow(() -> new IllegalArgumentException("无效的Token"));

        String method = request.getMethod();
        String url = request.getRequestURI();

        return false;
    }
}
