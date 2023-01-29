package com.devbamki.spbootrestapi.global.config;

import com.devbamki.spbootrestapi.domain.member.service.LoginService;
import com.devbamki.spbootrestapi.global.login.filter.JsonUserIdPasswordAuthenticationFilter;
import com.devbamki.spbootrestapi.global.login.handler.LoginFailureHandler;
import com.devbamki.spbootrestapi.global.login.handler.LoginSuccessJWTProvideHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoginService loginService;
    private final ObjectMapper objectMapper;

    public SecurityConfig(LoginService loginService, ObjectMapper objectMapper) {
        this.loginService = loginService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .formLogin().disable() // formLogin 인증방법 비활성화
                .httpBasic().disable()// httpBasic 인증방법 비활성화
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "signUp", "/")
                .permitAll()
                .anyRequest().authenticated();

        http.addFilterAfter(jsonUserIdPasswordLoginFilter(), LogoutFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler();
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUserIdPasswordAuthenticationFilter jsonUserIdPasswordLoginFilter(){
        JsonUserIdPasswordAuthenticationFilter jsonUserIdPasswordLoginFilter = new JsonUserIdPasswordAuthenticationFilter(objectMapper);
        jsonUserIdPasswordLoginFilter().setAuthenticationManager(authenticationManager());
        jsonUserIdPasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUserIdPasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());

        return jsonUserIdPasswordLoginFilter;
    }


}
