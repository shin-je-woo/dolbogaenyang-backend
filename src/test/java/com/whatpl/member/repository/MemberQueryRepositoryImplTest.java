package com.whatpl.member.repository;

import com.whatpl.BaseRepositoryTest;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.model.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MemberQueryRepositoryImplTest extends BaseRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("Member를 조회할 때 기술스택, 포트폴리오, 참고링크, 관심주제 모두 조회(Fetch Join)")
    void findMemberWithAllById() {
        // given
        Member member = MemberFixture.withAll();
        Long savedMemberId = memberRepository.save(member).getId();
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findMemberWithAllById(savedMemberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));

        // then
        assertEquals(member.getMemberSkills().size(), findMember.getMemberSkills().size());
        assertEquals(member.getMemberSubjects().size(), findMember.getMemberSubjects().size());
        assertEquals(member.getMemberReferences().size(), findMember.getMemberReferences().size());
        assertEquals(member.getMemberPortfolios().size(), findMember.getMemberPortfolios().size());
        assertNotNull(member.getMemberPortfolios());
        assertEquals(member, member.getMemberPortfolios().get(0).getMember());
    }
}