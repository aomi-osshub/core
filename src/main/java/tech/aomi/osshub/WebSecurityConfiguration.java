package tech.aomi.osshub;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import tech.aomi.osshub.api.TokenService;
import tech.aomi.osshub.auth.TokenAuthenticationFilter;
import tech.aomi.osshub.auth.TokenAuthenticationProvider;
import tech.aomi.osshub.constant.Common;

import java.util.Arrays;

/**
 * @author Sean Create At 2020/2/6
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private AuthenticationSuccessHandler authenticationSuccessHandler;
//
//    @Autowired
//    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Autowired
    private TokenService tokenService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        return new UserDetailsServiceImpl();
//    }

//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new TokenAuthenticationProvider(tokenService));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
//                .addFilterBefore(filterSecurityInterceptor, FilterSecurityInterceptor.class)
                .addFilterAfter(new TokenAuthenticationFilter(), BasicAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .antMatcher("/**")
                .authorizeRequests();

//        SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
//        logoutSuccessHandler.setTargetUrlParameter("redirect_uri");
//
        registry.and()
//                .formLogin()
//                .loginProcessingUrl("/system/login")
//                .successHandler(authenticationSuccessHandler)
//                .failureHandler(authenticationFailureHandler)
//                .permitAll()
//
//                .and()
//                .logout()
//                .logoutUrl("/system/logout")
//                .logoutSuccessHandler(logoutSuccessHandler)
//                .permitAll()
//
//                // 异常处理
//                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)

        ;

        Common.ALL_PERMISSION.forEach(permission -> {
            String[] authorities = Arrays.stream(permission.getAuthorities()).map(Enum::name).toArray(String[]::new);
            registry.antMatchers(permission.getMethod(), permission.getAntPatterns()).hasAnyAuthority(authorities);
        });

        if (StringUtils.isNotEmpty(webMvcProperties.getStaticPathPattern()) && !"/".equals(webMvcProperties.getStaticPathPattern())) {
            registry.antMatchers(HttpMethod.GET, webMvcProperties.getStaticPathPattern()).permitAll();
        }
        registry.antMatchers(HttpMethod.POST, "/tokens").permitAll();

        registry.anyRequest().authenticated();
    }

}
