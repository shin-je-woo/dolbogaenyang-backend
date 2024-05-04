package com.whatpl.chat.dto;

import com.whatpl.chat.domain.enums.ChatRoomType;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.member.domain.Member;
import com.whatpl.project.domain.Project;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomDto {

    private final Project project;
    private final Member applicant;
    private final ChatRoomType chatRoomType;
    private final Job job;

    @Builder
    public ChatRoomDto(Project project, Member applicant, ChatRoomType chatRoomType, Job job) {
        this.project = project;
        this.applicant = applicant;
        this.chatRoomType = chatRoomType;
        this.job = job;
    }
}
