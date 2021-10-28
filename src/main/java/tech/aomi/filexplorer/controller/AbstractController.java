package tech.aomi.filexplorer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tech.aomi.filexplorer.entity.Client;
import tech.aomi.filexplorer.entity.Token;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sean createAt 2021/10/27
 */
@Slf4j
public class AbstractController extends tech.aomi.common.web.controller.AbstractController {

    @Autowired
    protected HttpServletRequest request;


    protected Client client() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Token token = (Token) authentication.getCredentials();
        LOGGER.debug("token: {}", token);
        return token.getClient();
    }

}
