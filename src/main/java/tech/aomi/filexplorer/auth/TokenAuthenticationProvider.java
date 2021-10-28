package tech.aomi.filexplorer.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import tech.aomi.filexplorer.api.TokenService;
import tech.aomi.filexplorer.entity.Client;
import tech.aomi.filexplorer.entity.Token;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Sean createAt 2021/10/27
 */
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenService tokenService;

    public TokenAuthenticationProvider(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication.isAuthenticated()) {
            return authentication;
        }

        // 从 TokenAuthentication 中获取 token
        String tokenStr = authentication.getCredentials().toString();
        if (StringUtils.isEmpty(tokenStr)) {
            return authentication;
        }
        Token token = tokenService.findValidById(tokenStr).orElseThrow(() -> new BadCredentialsException("无效的token: " + tokenStr));

        Client client = token.getClient();

        List<GrantedAuthority> authorities = Optional.ofNullable(client.getAuthorities())
                .orElse(Collections.emptySet())
                .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        UserDetails user = User.builder()
                .username("api")
                .password("")
                .authorities(authorities)
                .build();

        // 返回新的认证信息，带上 token 和反查出的用户信息
        Authentication auth = new PreAuthenticatedAuthenticationToken(user, token, user.getAuthorities());
        auth.setAuthenticated(true);
        return auth;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (TokenAuthentication.class.isAssignableFrom(aClass));
    }
}
