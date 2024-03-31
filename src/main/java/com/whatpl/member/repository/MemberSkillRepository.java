package com.whatpl.member.repository;

import com.whatpl.member.domain.MemberSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberSkillRepository extends JpaRepository<MemberSkill, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from MemberSkill ms where ms.member.id = :memberId")
    void deleteByMemberId(long memberId);
}
