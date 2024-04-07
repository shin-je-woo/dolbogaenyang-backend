package com.whatpl.member.service;

import com.whatpl.attachment.domain.Attachment;
import com.whatpl.attachment.service.AttachmentService;
import com.whatpl.member.domain.MemberPortfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPortfolioService {

    private final AttachmentService attachmentService;

    @Transactional
    public List<MemberPortfolio> uploadPortfolio(List<MultipartFile> portfolios) {
        if (CollectionUtils.isEmpty(portfolios)) {
            return Collections.emptyList();
        }

        List<MemberPortfolio> memberPortfolios = new ArrayList<>();
        portfolios.parallelStream()
                .forEach(portfolio -> {
                    Long attachmentId = attachmentService.upload(portfolio);
                    // 1차 캐시에 있는 데이터 조회 (쿼리X)
                    Attachment attachment = attachmentService.findById(attachmentId);
                    MemberPortfolio memberPortfolio = new MemberPortfolio(attachment);
                    memberPortfolios.add(memberPortfolio);
                });
        return memberPortfolios;
    }
}
