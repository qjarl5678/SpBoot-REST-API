package com.devbamki.spbootrestapi.domain.member.repository;

import com.devbamki.spbootrestapi.domain.member.Member;
import com.devbamki.spbootrestapi.domain.member.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.apache.tomcat.util.IntrospectionUtils.clear;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;

    @AfterEach
    private void after(){
        entityManager.clear();
    }

    /** 회원가입 성공 테스트 코드 */
    @Test
    public void successLogic() throws Exception{
        // GIVEN
        Member member = Member.builder()
                .userId("devbamki")
                .userPw("1234")
                .userName("개발자")
                .nickName("개발자 지미킴")
                .role(Role.user)
                .age(20) // <- 20살 하고싶다..
                .build();

        // WHEN
        Member saveMember = memberRepository.save(member);

        // THEN
        Member findMember = memberRepository.findById(saveMember.getId())
                .orElseThrow(()-> new RuntimeException("저장된 회원이 없습니다."));

        assertThat(findMember).isSameAs(saveMember);
        assertThat(findMember).isSameAs(member);
    }

    /** 빈 값으로 시도 시 오류 (현재 테스트에서는 아이디 null만 진행)*/
    @Test
    public void emptyValueError() throws Exception{
        // GIVEN
        Member member = Member.builder().userPw("1234").userName("TEST").nickName("TEST").role(Role.user).age(20).build();

        // WHEN & THEN
        assertThrows(Exception.class, () -> memberRepository.save(member));
    }

    /** 아이디 중복 시 오류 */
    @Test
    public void duplicatedIdError() throws Exception{
        // GIVEN
        Member firstMember = Member.builder().userId("BK").userPw("1234").userName("TEST").nickName("TEST").role(Role.user).age(20).build();
        Member secondMember = Member.builder().userId("BK").userPw("1234").userName("TEST").nickName("TEST").role(Role.user).age(20).build();

        memberRepository.save(firstMember);
        clear();

        // WHEN & THEN
        assertThrows(Exception.class, ()->memberRepository.save(secondMember));
    }

    /** 회원 수정 성공 테스트 코드 */
    @Test
    public void changeMemberSuccess() throws Exception{

        // GIVEN
        Member member = Member.builder().userId("BK").userPw("1234").userName("TEST").nickName("TEST").role(Role.user).age(20).build();
        memberRepository.save(member);
        clear();

        String newPw = "12345";
        String newName = "NewBK";
        String newNickName = "NewTest";
        int newAge = 200;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // WHEN
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(()-> new Exception());
        findMember.changeUserName(newName);
        findMember.changePw(passwordEncoder, newPw);
        findMember.changeAge(newAge);
        findMember.changeNickName(newNickName);
        entityManager.flush();

        //THEN
        Member findChangedMember = memberRepository.findById(findMember.getId()).orElseThrow(()-> new Exception());
        assertThat(findChangedMember).isSameAs(findMember);
        assertThat(passwordEncoder.matches(newPw, findChangedMember.getUserPw())).isTrue();
        assertThat(findChangedMember.getUserName()).isEqualTo(newName);
        assertThat(findChangedMember.getUserName()).isNotEqualTo(member.getUserName());
    }

    /** 회원 삭제 성공 테스트 코드 */
    @Test
    public void deleteUserSuccess() throws Exception{

        // GIVEN
        Member member = Member.builder().userId("BK").userPw("1234").userName("TEST").nickName("TEST").role(Role.user).age(20).build();
        memberRepository.save(member);
        clear();

        // WHEN
        memberRepository.delete(member);
        clear();

        // THEN
        assertThrows(Exception.class, ()-> memberRepository.findById(member.getId()).orElseThrow(()-> new Exception()));
    }

    /** 회원 아이디로 존재 여부 판단 테스트 코드 */
    @Test
    public void existByUserId() throws Exception{

        // GIVEN
        String userId = "test";
        Member member = Member.builder().userId("BK").userPw("1234").userName("TEST").nickName("TEST").role(Role.user).age(20).build();
        memberRepository.save(member);
        clear();

        // WHEN & THEN
        assertThat(memberRepository.exitsByUserId(userId)).isTrue();
        assertThat(memberRepository.exitsByUserId(userId + "1232132132")).isFalse();
    }

    /** 회원 아이디로 정보 가져오기 */
    @Test
    public void findByUserId() throws Exception {
        // GIVEN
        String username = "test";
        Member member1 = Member.builder().userId(username).userPw("1234567890").userName("Member1").role(Role.user).nickName("NickName1").age(22).build();
        memberRepository.save(member1);
        clear();


        // WHEN & THEN
        assertThat(memberRepository.findByUserName(username).get().getUserId()).isEqualTo(member1.getUserId());
        assertThat(memberRepository.findByUserName(username).get().getUserName()).isEqualTo(member1.getUserName());
        assertThat(memberRepository.findByUserName(username).get().getId()).isEqualTo(member1.getId());
        assertThrows(Exception.class,
                () -> memberRepository.findByUserName(username+"123")
                        .orElseThrow(() -> new Exception()));
    }

    /** 회원가입 시 가입시간 등록 */
    @Test
    public void createTime() throws Exception{

        // GIVEN
        Member member = Member.builder().userId("user").userPw("1234567890").userName("Member1").role(Role.user).nickName("NickName1").age(22).build();
        memberRepository.save(member);
        clear();

        // WHEN
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(()-> new Exception());

        // THEN
        assertThat(findMember.getCreatedDate()).isNotNull();
        assertThat(findMember.getLastModifiedDate()).isNotNull();

    }




}