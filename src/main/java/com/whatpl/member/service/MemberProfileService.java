package com.whatpl.member.service;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.member.domain.Member;
import com.whatpl.member.domain.MemberPortfolio;
import com.whatpl.member.dto.NicknameDuplResponse;
import com.whatpl.member.dto.ProfileOptionalRequest;
import com.whatpl.member.dto.ProfileRequiredRequest;
import com.whatpl.member.repository.MemberRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final MemberPortfolioService memberPortfolioService;

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
        List<MemberPortfolio> memberPortfolios = memberPortfolioService.uploadPortfolio(portfolios);
        memberPortfolios.forEach(member::addMemberPortfolio);
        member.modifyMemberSubject(info.getSubjects());
        member.modifyMemberReference(info.getReferences());
        member.setWorkTime(info.getWorkTime());
    }
}
