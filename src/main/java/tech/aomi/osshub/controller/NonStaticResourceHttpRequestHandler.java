package tech.aomi.osshub.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * 动态资源处理
 *
 * @author Sean
 */
@Slf4j
@Component
public class NonStaticResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    public final static String ATTR_FILE = "NON-STATIC-FILE";

    @Override
    protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
        final Path filePath = (Path) request.getAttribute(ATTR_FILE);
        return new FileUrlResource(filePath.toString());
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            super.handleRequest(request, response);
        } catch (ClientAbortException e) {
            //
            LOGGER.debug("客户端终止异常: {}", e.getMessage());
        }
    }
}