package com.whatpl.member.domain;

import com.whatpl.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Enumerated(EnumType.STRING)
    private Job job;

    @Enumerated(EnumType.STRING)
    private Career career;

    private Boolean profileOpen;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberSkill> memberSkills = new LinkedHashSet<>();

    @Builder
    public Member(SocialType socialType, String socialId, String nickname, MemberStatus status,
                  Job job, Career career, Boolean profileOpen) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.nickname = nickname;
        this.status = status;
        this.job = job;
        this.career = career;
        this.profileOpen = profileOpen;
    }

    //==연관관계 메서드==//
    public void addMemberSkill(MemberSkill memberSkill) {
        if (memberSkill == null) {
            return;
        }
        this.memberSkills.add(memberSkill);
        memberSkill.setMember(this);
    }


    //==비즈니스 로직==//
    /**
     * 필수 정보 입력
     */
    public void modifyRequiredInfo(String nickname, Job job, Career career, boolean profileOpen, Set<MemberSkill> memberSkills) {
        this.nickname = nickname;
        this.job = job;
        this.career = career;
        this.profileOpen = profileOpen;
        memberSkills.forEach(this::addMemberSkill);
    }

    /**
     * 프로필 작성 여부
     * 필수 정보만 입력하면 작성한 것으로 판단
     */
    public boolean hasProfile() {
        return (!CollectionUtils.isEmpty(memberSkills) &&
                getJob() != null &&
                getCareer() != null &&
                getNickname() != null &&
                getProfileOpen() != null);
    }
}