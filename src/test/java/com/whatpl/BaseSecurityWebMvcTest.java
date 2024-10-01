package com.whatpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatpl.domain.attachment.service.AttachmentService;
import com.whatpl.domain.chat.service.ChatMessageService;
import com.whatpl.domain.chat.service.ChatRoomService;
import com.whatpl.domain.member.service.*;
import com.whatpl.domain.project.service.*;
import com.whatpl.domain.whatplpople.service.WhatplpeopleService;
import com.whatpl.external.upload.FileUploader;
import com.whatpl.global.common.properties.ImageProperties;
import com.whatpl.global.common.properties.JwtProperties;
import com.whatpl.global.config.SecurityConfig;
import com.whatpl.global.jwt.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.when;

@WebMvcTest
@Import(SecurityConfig.class)
public abstract class BaseSecurityWebMvcTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected MemberLoginService memberLoginService;

    @MockBean
    protected JwtService jwtService;

    @MockBean
    protected JwtProperties jwtProperties;

    @MockBean
    protected AttachmentService attachmentService;

    @MockBean
    protected MemberProfileService memberProfileService;

    @MockBean
    protected ProjectWriteService projectWriteService;

    @MockBean
    protected ProjectApplyService projectApplyService;

    @MockBean
    protected ProjectReadService projectReadService;

    @MockBean
    protected ProjectCommentService projectCommentService;

    @MockBean
    protected FileUploader fileUploader;

    @MockBean
    protected ProjectLikeService projectLikeService;

    @MockBean
    protected ChatRoomService chatRoomService;

    @MockBean
    protected ChatMessageService chatMessageService;

    @MockBean
    protected ProjectParticipantService projectParticipantService;

    @MockBean
    protected ImageProperties imageProperties;

    @MockBean
    protected WhatplpeopleService whatplpeopleService;

    @MockBean
    protected MemberProjectService memberProjectService;

    @MockBean
    protected MemberPortfolioService memberPortfolioService;

    @MockBean
    protected MemberPictureService memberPictureService;

    @BeforeEach
    protected void init() {
        when(jwtProperties.getTokenType()).thenReturn("Bearer");
    }

    protected MockMultipartFile createMockMultipartFile(String key, String filename, String mimeType) throws IOException {
        String path = requireNonNull(getClass().getClassLoader().getResource("files/" + filename)).getPath();
        InputStream inputStream = new FileInputStream(path);
        return new MockMultipartFile(key, filename, mimeType, inputStream);
    }
}
