package com.whatpl.domain.attachment.domain;

public interface AttachmentUrlParser {
    boolean supports(AttachmentUrlParseType type);
    String parseUrl(Long id);
}
