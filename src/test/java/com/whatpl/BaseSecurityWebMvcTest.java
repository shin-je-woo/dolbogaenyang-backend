package com.whatpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatpl.attachment.service.AttachmentService;
import com.whatpl.chat.service.ChatMessageService;
import com.whatpl.chat.service.ChatRoomService;
import com.whatpl.global.config.SecurityConfig;
import com.whatpl.global.image.ImageProperties;
import com.whatpl.global.jwt.JwtProperties;
import com.whatpl.global.jwt.JwtService;
import com.whatpl.global.upload.S3Uploader;
import com.whatpl.member.service.MemberLoginService;
import com.whatpl.member.service.MemberProfileService;
import com.whatpl.project.service.*;
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
    protected S3Uploader s3Uploader;

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
