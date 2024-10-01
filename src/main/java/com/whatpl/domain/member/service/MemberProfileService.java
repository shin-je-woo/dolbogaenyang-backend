package com.whatpl.domain.member.service;

import com.whatpl.domain.attachment.event.AttachmentDeleteEvent;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.domain.MemberEditor;
import com.whatpl.domain.member.domain.MemberPortfolio;
import com.whatpl.domain.member.dto.*;
import com.whatpl.domain.member.mapper.MemberMapper;
import com.whatpl.domain.member.repository.MemberRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final MemberPortfolioService memberPortfolioService;
    private final MemberPictureService memberPictureService;
    private final ApplicationEventPublisher publisher;
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public NicknameDuplResponse nicknameDuplCheck(final String nickname) {
        AtomicReference<NicknameDuplResponse> result = new AtomicReference<>();
        memberRepository.findByNickname(nickname)
                .ifPresentOrElse(
                        member -> result.set(NicknameDuplResponse.of(false, nickname)),
                        () -> result.set(NicknameDuplResponse.of(true, nickname))
                );
        return result.get();
    }

    @Transactional
    public void updateRequiredProfile(final ProfileRequiredRequest request, final long memberId) {
        Member member = memberRepository.findMemberWithAllById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        member.modifyMemberSkills(request.getSkills());
        member.modifyRequiredInfo(request.getNickname(), request.getJob(),
                request.getCareer(), request.isProfileOpen());
    }

    @Transactional
    public void updateOptionalProfile(@NonNull final ProfileOptionalRequest info, final List<MultipartFile> portfolios, final long memberId) {
        Member member = memberRepository.findMemberWithAllById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        memberPortfolioService.uploadPortfolio(member, portfolios);
        member.modifyMemberSubject(info.getSubjects());
        member.modifyMemberReference(info.getReferences());
        member.modifyWorkTime(info.getWorkTime());
    }

    @Transactional
    public void updateProfile(ProfileUpdateRequest request, final List<MultipartFile> portfolios, final long memberId) {
        Member member = memberRepository.findMemberWithAllById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        MemberEditor memberEditor = getMemberEditor(request, member);
        member.updateProfile(memberEditor, request.getSubjects(), request.getReferences(), request.getSkills());
        List<MemberPortfolio> deletedPortfolios = member.deletePortfolios(request.getDeletePortfolioIds());
        memberPortfolioService.uploadPortfolio(member, portfolios);
        publishPortfolioDeleteEvent(deletedPortfolios);
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse readProfile(final long memberId) {
        Member member = memberRepository.findMemberWithAllById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        return memberMapper.toMemberProfileResponse(member);
    }

    @Transactional
    public void updatePicture(long memberId, MultipartFile picture) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_MEMBER));
        memberPictureService.modifyPicture(member, picture);
    }

    private MemberEditor getMemberEditor(ProfileUpdateRequest request, Member member) {
        return member.toEditor()
                .nickname(request.getNickname())
                .job(request.getJob())
                .career(request.getCareer())
                .profileOpen(request.isProfileOpen())
                .workTime(request.getWorkTime())
                .build();
    }

    private void publishPortfolioDeleteEvent(List<MemberPortfolio> deletedPortfolios) {
        if (CollectionUtils.isEmpty(deletedPortfolios)) return;
        deletedPortfolios.forEach(portfolio ->
                publisher.publishEvent(AttachmentDeleteEvent.from(portfolio.getAttachment().getStoredName()))
        );
    }
}
