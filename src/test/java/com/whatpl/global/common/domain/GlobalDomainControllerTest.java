package com.whatpl.global.common.domain;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.security.model.WithMockWhatplMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class GlobalDomainControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("글로벌 도메인 조회 API Docs")
    void domains() throws Exception {
        mockMvc.perform(get("/domains")
                        .header(AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("read-global-domains",
                        resourceDetails().tag(ApiDocTag.DOMAIN.getTag())
                                .summary("글로벌 도메인 조회"),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("jobs").type(JsonFieldType.ARRAY).description("직무"),
                                fieldWithPath("careers").type(JsonFieldType.ARRAY).description("경력"),
                                fieldWithPath("skills").type(JsonFieldType.ARRAY).description("기술 스택"),
                                fieldWithPath("subjects").type(JsonFieldType.ARRAY).description("관심주제(도메인)"),
                                fieldWithPath("workTimes").type(JsonFieldType.ARRAY).description("작업 가능 시간")
                        )
                ));
    }
}