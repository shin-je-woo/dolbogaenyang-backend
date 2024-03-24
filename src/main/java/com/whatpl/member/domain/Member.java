package com.whatpl.member.domain;

import com.whatpl.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    public Member(SocialType socialType, String socialId, String nickname, MemberStatus status) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.nickname = nickname;
        this.status = status;
    }
}