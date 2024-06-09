package com.whatpl.global.image;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationExtension;

import java.io.IOException;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ImageControllerTest extends BaseSecurityWebMvcTest {

    ImageProperties.Prefix prefix = new ImageProperties.Prefix("DEFAULT_IMAGE_PROJECT", "DEFAULT_IMAGE_SKILL");

    @BeforeEach
    void setUp() throws IOException {
        when(imageProperties.getPrefix()).thenReturn(prefix);
        InputStreamResource resource = new InputStreamResource(createMockMultipartFile("file", "cat.jpg", IMAGE_JPEG_VALUE).getInputStream());
        when(s3Uploader.download(any())).thenReturn(resource);
    }

    @Test
    @DisplayName("프로젝트 기본 이미지 API Docs")
    void projectImage() throws Exception {
        // expected
        mockMvc.perform(get("/images/project"))
                .andExpectAll(
                        status().isOk(),
                        header().exists(HttpHeaders.CACHE_CONTROL)
                )
                .andDo(print())
                .andDo(document("image-project",
                        resourceDetails().tag(ApiDocTag.IMAGE.getTag())
                                .summary("프로젝트 기본 이미지")
                ));
    }

    @Test
    @DisplayName("기술스택 기본 이미지 API Docs")
    void skillImage() throws Exception {
        // expected
        mockMvc.perform(get("/images/skill/{skillName}", "JavaScript"))
                .andExpectAll(
                        status().isOk(),
                        header().exists(HttpHeaders.CACHE_CONTROL)
                )
                .andDo(print())
                .andDo(document("image-skill",
                        resourceDetails().tag(ApiDocTag.IMAGE.getTag())
                                .summary("기술스택 기본 이미지")
                ));
    }
}