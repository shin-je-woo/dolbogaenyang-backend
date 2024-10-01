package com.whatpl.domain.attachment.controller;

import com.whatpl.domain.attachment.dto.ResourceDto;
import com.whatpl.domain.member.service.MemberPictureService;
import com.whatpl.domain.member.service.MemberPortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class MemberAttachmentController {

    private final MemberPortfolioService memberPortfolioService;
    private final MemberPictureService memberPictureService;

    @GetMapping("/portfolios/{portfolioId}")
    public ResponseEntity<Resource> portfolio(@PathVariable Long portfolioId) {
        ResourceDto resourceDto = memberPortfolioService.readPortfolio(portfolioId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, resourceDto.getMimeType())
                .body(resourceDto.getResource());
    }

    @GetMapping("/pictures/{pictureId}")
    public ResponseEntity<Resource> picture(@PathVariable Long pictureId) {
        ResourceDto resourceDto = memberPictureService.readPicture(pictureId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, resourceDto.getMimeType())
                .body(resourceDto.getResource());
    }
}
