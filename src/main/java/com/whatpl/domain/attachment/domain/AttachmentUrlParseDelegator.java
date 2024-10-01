package com.whatpl.domain.attachment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttachmentUrlParseDelegator {

    private final List<AttachmentUrlParser> attachmentUrlParsers;

    public String parseUrl(AttachmentUrlParseType type, Long id) {
        for (AttachmentUrlParser attachmentUrlParser : attachmentUrlParsers) {
            if (attachmentUrlParser.supports(type)) {
                return attachmentUrlParser.parseUrl(id);
            }
        }
        throw new IllegalStateException("일치하는 UrlParser가 존재하지 않습니다.");
    }
}
