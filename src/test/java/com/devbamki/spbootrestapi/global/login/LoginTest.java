package com.devbamki.spbootrestapi.global.login;

import com.devbamki.spbootrestapi.domain.member.Member;
import com.devbamki.spbootrestapi.domain.member.Role;
import com.devbamki.spbootrestapi.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    PasswordEncoder delegatingPasswordEncorder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_USERID = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERID = "devbamki";
    private static String PASSWORD = "1234";
    private static String LOGIN_URL = "/login";

    private void clear(){
        entityManager.flush();
        entityManager.clear();
    }

    @BeforeEach
    private void init(){
        memberRepository.save(Member.builder()
                .userId(USERID)
                .userPw(delegatingPasswordEncorder.encode(PASSWORD))
                .userName("BAMKI")
                .nickName("DEVBAMKI")
                .role(Role.user)
                .age(20)
                .build());
        clear();
    }

    private Map getUserIdPasswordMap(String user_id, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERID, user_id);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    private ResultActions perform(String url, MediaType mediaType, Map userIdPasswordMap) throws Exception{
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .contentType(mediaType)
                        .content(objectMapper.writeValueAsString(userIdPasswordMap))
        );
    }


    /** 로그인 성공 시 */
    @Test
    public void loginSuccess() throws Exception{
        // GIVEN
        Map<String, String> map = getUserIdPasswordMap(USERID, PASSWORD);

        // WHEN & THEN
        MvcResult result = perform(LOGIN_URL, MediaType.APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    /** 로그인 실패 - 아이디 오류 */
    @Test
    public void loginFailById() throws Exception{
        // GIVEN
        Map<String, String> map = getUserIdPasswordMap(USERID+"123", PASSWORD);

        // WHEN & THEN
        MvcResult result = perform(LOGIN_URL, MediaType.APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    /** 로그인 실패 - 패스워드 오류 */
    @Test
    public void loginFailByPassword() throws Exception{
        Map<String, String> map = getUserIdPasswordMap(USERID, PASSWORD+"123");

        // WHEN & THEN
        MvcResult result = perform(LOGIN_URL, MediaType.APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    /** 로그인 실패 - 잘못된 URL 요청 */
    @Test
    public void urlErrorForbidden() throws Exception{
        //GIVEN
        Map<String,String> map = getUserIdPasswordMap(USERID,PASSWORD);

        // WHEN & THEN
        perform(LOGIN_URL+"123", MediaType.APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    /** 로그인 실패 - JSON 타입이 아니면 200 코드 반황(보안땜시) */
    public void loginFailByJsonType() throws Exception{
        //GIVEN
        Map<String, String> map = getUserIdPasswordMap(USERID, PASSWORD);

        // WHEN & THEN
        MvcResult result = perform(LOGIN_URL, MediaType.APPLICATION_FORM_URLENCODED, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    /** 로그인 실패 - 메소드가 GET */
    public void loginFailGetMethod() throws Exception{
        //GIVEN
        Map<String, String> map = getUserIdPasswordMap(USERID,PASSWORD);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders
                .get(LOGIN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /** 로그인 실패 - 메소드가 PUT */
    public void loginFailPutMethod() throws Exception{
        //GIVEN
        Map<String, String> map = getUserIdPasswordMap(USERID, PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
