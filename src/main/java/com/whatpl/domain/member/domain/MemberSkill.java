package com.whatpl.domain.member.domain;

import com.whatpl.global.common.model.BaseTimeEntity;
import com.whatpl.global.common.model.Skill;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "member_skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSkill extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MemberSkill(Skill skill) {
        this.skill = skill;
    }

    public void addRelation(@NonNull Member member) {
        this.member = member;
    }
}
