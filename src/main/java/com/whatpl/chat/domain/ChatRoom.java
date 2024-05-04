package com.whatpl.chat.domain;

import com.whatpl.chat.domain.enums.ChatRoomType;
import com.whatpl.global.common.BaseTimeEntity;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType type;

    @Enumerated(EnumType.STRING)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Member applicant;

    @Builder
    public ChatRoom(ChatRoomType type, Job job, Project project, Member applicant) {
        this.type = type;
        this.job = job;
        this.project = project;
        this.applicant = applicant;
    }
}
