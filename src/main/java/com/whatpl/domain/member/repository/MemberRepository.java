package com.whatpl.domain.member.repository;

import com.whatpl.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    Optional<Member> findByNickname(String nickname);

    @Query("select m from Member m left join fetch m.picture where m.picture.id = :pictureId")
    Optional<Member> findByPictureId(Long pictureId);
}
