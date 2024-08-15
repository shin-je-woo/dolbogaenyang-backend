package com.whatpl.domain.member.domain;

import com.whatpl.global.common.model.BaseTimeEntity;
import com.whatpl.global.common.model.Subject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Entity
@Table(name = "member_subject")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSubject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MemberSubject(Subject subject) {
        this.subject = subject;
    }

    public void addRelation(@NonNull Member member) {
        this.member = member;
    }
}
