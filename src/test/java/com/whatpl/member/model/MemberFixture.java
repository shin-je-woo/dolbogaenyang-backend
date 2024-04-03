package com.whatpl.member.model;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.member.domain.*;
import org.springframework.http.MediaType;

import java.util.UUID;

public class MemberFixture {

    public static Member withAll() {
        Member member = Member.builder()
                .socialType(SocialType.NAVER)
                .socialId("왓플테스트유저")
                .nickname("왓플테스트유저")
                .status(MemberStatus.ACTIVE)
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.NONE)
                .workTime(WorkTime.LESS_THAN_TEN)
                .profileOpen(true)
                .build();

        member.addMemberSkill(new MemberSkill(Skill.JAVA));
        member.addMemberSubject(new MemberSubject(Subject.HEALTH));
        member.addMemberReference(new MemberReference("https://github.com"));
        Attachment attachment = Attachment.builder()
                .fileName("cat.jpg")
                .storedName(UUID.randomUUID().toString())
                .mimeType(MediaType.IMAGE_JPEG_VALUE)
                .build();
        member.addMemberPortfolio(new MemberPortfolio(attachment));
        return member;
    }
}
