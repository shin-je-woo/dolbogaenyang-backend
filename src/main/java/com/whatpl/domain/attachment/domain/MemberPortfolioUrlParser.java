package com.whatpl.domain.attachment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MemberPortfolioUrlParser implements AttachmentUrlParser {

    @Value("${server-url}")
    private String serverUrl;

    @Override
    public boolean supports(AttachmentUrlParseType type) {
        return Objects.equals(type, AttachmentUrlParseType.MEMBER_PORTFOLIO);
    }

    @Override
    public String parseUrl(Long id) {
        if (id == null) return null;
        return String.format("%s/attachments/portfolios/%d", serverUrl, id);
    }
}
