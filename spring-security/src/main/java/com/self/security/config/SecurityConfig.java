package com.self.security.config;

import com.self.security.filter.AuthenticationTokenFilter;
import com.self.security.handle.AuthenticationFailedEntryPoint;
import com.self.security.handle.SysLogoutSuccessHandler;
import com.self.security.provider.SmsAuthenticationProvider;
import com.self.security.service.SysUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 配置
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SysUserDetailsService sysUserDetailsService;

    @Autowired
    private AuthenticationFailedEntryPoint authenticationFailedEntryPoint;

    @Autowired
    private SysLogoutSuccessHandler sysLogoutSuccessHandler;

    @Autowired
    private AuthenticationTokenFilter authenticationTokenFilter;

    @Autowired
    private SmsAuthenticationProvider smsAuthenticationProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                //不使用session，禁用csrf
                csrf().disable()

                //认证失败处理
                .exceptionHandling().authenticationEntryPoint(authenticationFailedEntryPoint)
                .and()

                //基于token，不使用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                //过滤请求
                .authorizeRequests()

                //对指定路径请求允许任意访问
                .antMatchers("/api/token/**").permitAll()

                //对前端静态资源查询请求允许任意访问
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()

                //对swagger配置请求允许匿名访问
                .antMatchers("/swagger-ui/**").anonymous()
                .antMatchers("/swagger-resources/**").anonymous()
                .antMatchers("/v3/api-docs/**").anonymous()

                //对druid数据连接池请求允许匿名访问
                .antMatchers("/druid/**").anonymous()

                //对健康检查请求允许匿名访问
                .antMatchers("/actuator/**").anonymous()

                //除以上请求外所有请求都必须身份认证后才能访问
                .anyRequest().authenticated()
                .and()

                //开启cors支持(跨域)
                .cors().configurationSource(corsConfigurationSource())
                .and()

                //禁止iframe调用
                .headers().frameOptions().disable();

        //配置退出处理
        httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(sysLogoutSuccessHandler);

        //配置token过滤器
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 认证入口
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                //用户名密码认证
                .userDetailsService(sysUserDetailsService)
                .passwordEncoder(noOpPasswordEncoder())
                .and()

                //验证码认证
                .authenticationProvider(smsAuthenticationProvider);
    }

    /**
     * 密码处理器
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 密码处理器
     */
    @Bean
    public PasswordEncoder noOpPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Cors配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

}
