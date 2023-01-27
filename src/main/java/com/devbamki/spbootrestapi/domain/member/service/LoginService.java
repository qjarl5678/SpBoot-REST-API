package com.devbamki.spbootrestapi.domain.member.service;

import com.devbamki.spbootrestapi.domain.member.Member;
import com.devbamki.spbootrestapi.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {
    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserName(username).orElseThrow(()->new UsernameNotFoundException("아이디가 없습니다."));

        return User.builder().username(member.getUserName())
                .password(member.getUserPw())
                .roles(member.getRole().name())
                .build();
    }
}
