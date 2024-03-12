package com.whatpl.member.domain;

import com.whatpl.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    private String nickname;

    private String profileImage;

    private Integer career;

    private String githubLink;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    public Member(SocialType socialType, String socialId, String nickname, String profileImage, Integer career, String githubLink, MemberStatus status) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.career = career;
        this.githubLink = githubLink;
        this.status = status;
    }
}