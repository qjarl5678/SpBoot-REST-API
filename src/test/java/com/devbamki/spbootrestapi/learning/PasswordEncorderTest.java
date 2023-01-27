package com.devbamki.spbootrestapi.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class PasswordEncorderTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    private static String PASSWORD = "devBamki";

    /** 패스워드 암호화 */
    @Test
    public void password_encrypt() throws Exception{

        // WHEN
        String encodePassword = passwordEncoder.encode(PASSWORD);

        // THEN
        assertThat(encodePassword).startsWith("{");
        assertThat(encodePassword).contains("{bcrypt}");
        assertThat(encodePassword).isNotEqualTo(PASSWORD);
    }

    @Test
    public void passwordRandomEncode() throws Exception {

        //when
        String encodePassword = passwordEncoder.encode(PASSWORD);
        String encodePassword2 = passwordEncoder.encode(PASSWORD);

        //then
        assertThat(encodePassword).isNotEqualTo(encodePassword2);

    }


    @Test
    public void encodedPassowordMatch() throws Exception {

        //when
        String encodePassword = passwordEncoder.encode(PASSWORD);

        //then
        assertThat(passwordEncoder.matches(PASSWORD, encodePassword)).isTrue();

    }


}
