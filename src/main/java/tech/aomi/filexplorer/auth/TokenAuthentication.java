package tech.aomi.filexplorer.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * token 认证
 *
 * @author Sean createAt 2021/10/27
 */
public class TokenAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 5409923538180040235L;

    private String token;

    public TokenAuthentication(String token) {
        this(token, null);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public TokenAuthentication(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
    }


    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
