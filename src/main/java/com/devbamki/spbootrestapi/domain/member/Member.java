package com.devbamki.spbootrestapi.domain.member;


import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Table(name= "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; // PK 설정

    @Column(nullable = false, length = 50, unique = true)
    private String userId; //유저 아이디

    private String userPw; //유저 비밀번호

    @Column(nullable = false, length = 50)
    private String userName; //유저 이름

    @Column(nullable = false, length = 30)
    private String nickName; //유저 닉네임

    @Column(nullable = false, length = 30)
    private Integer age; //유저 나이

    @Enumerated(EnumType.STRING)
    private Role role; //권한

    /** 사용자 정보 수정 */
    public void changePw(PasswordEncoder passwordEncoder, String userPw){
        this.userPw = passwordEncoder.encode(userPw);
    }
    public void changeUserName(String userName){
        this.userName = userName;
    }

    public void changeNickName(String nickName){
        this.nickName = nickName;
    }

    public void changeAge(int age){
        this.age = age;
    }

    /** 패스워드 암호화*/
    public void PasswordEncode(PasswordEncoder passwordEncoder){
        this.userPw = passwordEncoder.encode(userPw);
    }

}
