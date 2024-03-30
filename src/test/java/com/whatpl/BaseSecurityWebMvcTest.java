package com.whatpl;

import com.whatpl.attachment.service.AttachmentService;
import com.whatpl.global.config.SecurityConfig;
import com.whatpl.global.jwt.JwtProperties;
import com.whatpl.global.jwt.JwtService;
import com.whatpl.member.service.MemberLoginService;
import com.whatpl.member.service.MemberProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(SecurityConfig.class)
public class BaseSecurityWebMvcTest {

    @Autowired
    protected MockMvc mockMvc;

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
}
