package com.whatpl.domain.member.service;

import com.whatpl.domain.attachment.domain.Attachment;
import com.whatpl.domain.attachment.dto.ResourceDto;
import com.whatpl.domain.attachment.service.AttachmentService;
import com.whatpl.domain.member.domain.Member;
import com.whatpl.domain.member.domain.MemberPortfolio;
import com.whatpl.domain.member.repository.MemberPortfolioRepository;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPortfolioService {

    private final AttachmentService attachmentService;
    private final MemberPortfolioRepository memberPortfolioRepository;

    @Transactional
    public void uploadPortfolio(Member member, List<MultipartFile> portfolios) {
        if (CollectionUtils.isEmpty(portfolios)) {
            return;
        }
        portfolios.parallelStream()
                .forEach(portfolio -> {
                    Attachment attachment = attachmentService.upload(portfolio);
                    MemberPortfolio memberPortfolio = new MemberPortfolio(attachment);
                    member.addMemberPortfolio(memberPortfolio);
                });
    }

    @Transactional(readOnly = true)
    public ResourceDto readPortfolio(Long portfolioId) {
        MemberPortfolio memberPortfolio = memberPortfolioRepository.findByIdWithAttachment(portfolioId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA));
        Attachment attachment = memberPortfolio.getAttachment();
        return attachmentService.download(attachment.getId());
    }
}
