package tech.aomi.filexplorer.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.aomi.filexplorer.constant.HttpHeader;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Sean createAt 2021/10/27
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String DEFAULT_TOKEN_PARAMETER = "token";

    private String tokenParameter;

    public TokenAuthenticationFilter() {
        this(DEFAULT_TOKEN_PARAMETER);
    }

    public TokenAuthenticationFilter(String token) {
        this.tokenParameter = token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            // do nothing
        } else {
            String token;
            // ②
            Map<String, String[]> params = req.getParameterMap();
            if (!params.isEmpty() && params.containsKey(tokenParameter)) {
                token = params.get(tokenParameter)[0];
            } else {
                token = req.getHeader(HttpHeader.TOKEN);
            }

            if (token != null) {
                Authentication auth = new TokenAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            req.setAttribute("tech.aomi.filexplorer.auth.TokenAuthenticationFilter.FILTERED", true); //③
        }

        fc.doFilter(req, res); //④
    }
}
