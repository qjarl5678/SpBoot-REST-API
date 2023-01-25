package com.devbamki.spbootrestapi.domain.member.repository;

import com.devbamki.spbootrestapi.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserName(String userName);
    boolean exitsByUserId(String userId);
}
