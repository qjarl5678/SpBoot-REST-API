package com.devbamki.spbootrestapi.global.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login"; // /login/oauth2/* 로 오는 요청을 처리
    private static final String HTTP_METHOD = "POST"; // HTTP 메소드는 POST
    private static final String CONTENT_TYPE = "application/json"; // json 타입의 데이터로만 로그인 진행

    private final ObjectMapper objectMapper;

    private static final String USER_ID_KEY="userId";
    private static final String PASSWORD_KEY="password";


    /** /login으로 온 Post 요청에 매칭 */
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper){
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER); // 위에서 설정한 /login/oauth2/* 의 요청에 GET 요청 처리

        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getContentType() == null || request.getContentType().equals(CONTENT_TYPE)){
            throw new AuthenticationServiceException("Authentication Content-Type not supported:" + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> userIdPassowrdMap = objectMapper.readValue(messageBody, Map.class);

        String userId = userIdPassowrdMap.get(USER_ID_KEY);
        String password = userIdPassowrdMap.get(PASSWORD_KEY);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userId, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
