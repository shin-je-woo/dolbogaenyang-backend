package com.whatpl.member.domain;

import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    @Setter
    @Enumerated(EnumType.STRING)
    private WorkTime workTime;

    private Boolean profileOpen;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberSkill> memberSkills = new LinkedHashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberSubject> memberSubjects = new LinkedHashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberReference> memberReferences = new LinkedHashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPortfolio> memberPortfolios = new LinkedList<>();

    @Builder
    public Member(SocialType socialType, String socialId, String nickname, MemberStatus status,
                  Job job, Career career, WorkTime workTime, Boolean profileOpen) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.nickname = nickname;
        this.status = status;
        this.job = job;
        this.career = career;
        this.workTime = workTime;
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

    public void addMemberSubject(MemberSubject memberSubject) {
        if (memberSubject == null) {
            return;
        }
        if (memberReferences.size() >= 5) {
            throw new BizException(ErrorCode.MAX_SUBJECT_SIZE_EXCEED);
        }
        this.memberSubjects.add(memberSubject);
        memberSubject.setMember(this);
    }

    public void addMemberReference(MemberReference memberReference) {
        if (memberReference == null) {
            return;
        }
        if (memberReferences.size() >= 3) {
            throw new BizException(ErrorCode.MAX_REFERENCE_SIZE_EXCEED);
        }
        this.memberReferences.add(memberReference);
        memberReference.setMember(this);
    }

    public void addMemberPortfolio(MemberPortfolio memberPortfolio) {
        if (memberPortfolio == null) {
            return;
        }
        if (memberPortfolios.size() >= 5) {
            throw new BizException(ErrorCode.MAX_PORTFOLIO_SIZE_EXCEED);
        }
        this.memberPortfolios.add(memberPortfolio);
        memberPortfolio.setMember(this);
    }

    //==비즈니스 로직==//
    /**
     * 필수 정보 입력
     */
    public void modifyRequiredInfo(String nickname, Job job, Career career, boolean profileOpen) {
        this.nickname = nickname;
        this.job = job;
        this.career = career;
        this.profileOpen = profileOpen;
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

    /**
     * memberSkills를 추가/삭제한다.
     */
    public void modifyMemberSkills(Set<Skill> skills) {
        // skills 비어있을 경우 -> 전부 삭제
        if (CollectionUtils.isEmpty(skills)) {
            memberSkills.clear();
            return;
        }

        // member에 있던 skill이 skills에 존재하지 않는 경우 -> 삭제
        memberSkills.stream()
                .filter(memberSkill -> !skills.contains(memberSkill.getSkill()))
                .forEach(removeSkill -> memberSkills.remove(removeSkill));

        // member에 없던 skill이 skills에 존재하는 경우 -> 추가
        Set<Skill> exSkills = memberSkills.stream()
                .map(MemberSkill::getSkill)
                .collect(Collectors.toSet());
        skills.stream()
                .filter(skill -> !exSkills.contains(skill))
                .map(MemberSkill::new)
                .forEach(this::addMemberSkill);
    }

    /**
     * memberSubjects를 추가/삭제한다.
     */
    public void modifyMemberSubject(Set<Subject> subjects) {
        // subjects가 비어있을 경우 -> 전부 삭제
        if (CollectionUtils.isEmpty(subjects)) {
            memberSubjects.clear();
            return;
        }

        // member에 있던 subject가 subjects에 존재하지 않는 경우 -> 삭제
        memberSubjects.stream()
                .filter(memberSubject -> !subjects.contains(memberSubject.getSubject()))
                .forEach(removeSubject -> memberSubjects.remove(removeSubject));

        // member에 없던 subject가 subjects에 존재하는 경우 -> 추가
        Set<Subject> exSubjects = memberSubjects.stream()
                .map(MemberSubject::getSubject)
                .collect(Collectors.toSet());
        subjects.stream()
                .filter(subject -> !exSubjects.contains(subject))
                .map(MemberSubject::new)
                .forEach(this::addMemberSubject);
    }

    /**
     * memberReferences를 추가/삭제한다.
     */
    public void modifyMemberReference(Set<String> references) {
        // references 비어있을 경우 -> 전부 삭제
        if (CollectionUtils.isEmpty(references)) {
            memberReferences.clear();
            return;
        }

        // member에 있던 reference가 references에 존재하지 않는 경우 -> 삭제
        memberReferences.stream()
                .filter(memberReference -> !references.contains(memberReference.getReference()))
                .forEach(removeReference -> memberReferences.remove(removeReference));

        // member에 없던 reference가 references에 존재하는 경우 -> 추가
        Set<String> exReferences = memberReferences.stream()
                .map(MemberReference::getReference)
                .collect(Collectors.toSet());
        references.stream()
                .filter(reference -> !exReferences.contains(reference))
                .map(MemberReference::new)
                .forEach(this::addMemberReference);
    }
}