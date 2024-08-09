package com.whatpl.domain.member.domain;

import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.global.common.domain.enums.Skill;
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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MemberSkill(Skill skill) {
        this.skill = skill;
    }
}
