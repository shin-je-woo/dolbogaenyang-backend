package com.whatpl.domain.member.mapper;

import com.whatpl.domain.attachment.domain.AttachmentUrlParseDelegator;
import com.whatpl.domain.attachment.domain.AttachmentUrlParseType;
import com.whatpl.domain.member.domain.*;
import com.whatpl.domain.member.dto.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final AttachmentUrlParseDelegator attachmentUrlParseDelegator;

    public MemberProfileResponse toMemberProfileResponse(Member member) {
        return MemberProfileResponse.builder()
                .nickname(member.getNickname())
                .job(member.getJob())
                .career(member.getCareer())
                .profileOpen(member.getProfileOpen())
                .workTime(member.getWorkTime())
                .skills(member.getMemberSkills().stream().map(MemberSkill::getSkill).toList())
                .subjects(member.getMemberSubjects().stream().map(MemberSubject::getSubject).toList())
                .references(member.getMemberReferences().stream().map(MemberReference::getReference).toList())
                .portfolioUrls(buildPortfolioUrl(member))
                .pictureUrl(getBuildPictureUrl(member))
                .build();
    }

    private List<String> buildPortfolioUrl(Member member) {
        return member.getMemberPortfolios().stream()
                .map(MemberPortfolio::getId)
                .map(portfolioId -> attachmentUrlParseDelegator.parseUrl(AttachmentUrlParseType.MEMBER_PORTFOLIO, portfolioId))
                .toList();
    }

    private String getBuildPictureUrl(Member member) {
        return attachmentUrlParseDelegator.parseUrl(AttachmentUrlParseType.MEMBER_PICTURE, member.getPicture().getId());
    }
}
