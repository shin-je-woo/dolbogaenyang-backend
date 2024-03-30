package com.whatpl.member.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.ApiDocUtils;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.member.dto.NicknameDuplResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class MemberControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockUser
    @DisplayName("닉네임 중복 확인 API Docs")
    void checkNickname() throws Exception {
        // given
        String nickname = "신제우";
        NicknameDuplResponse nicknameDuplResponse = NicknameDuplResponse.of(true, nickname);
        when(memberProfileService.nicknameDuplCheck(nickname))
                .thenReturn(nicknameDuplResponse);
        when(jwtProperties.getTokenType()).thenReturn("Bearer");

        // expected
        mockMvc.perform(get("/members/check-nickname")
                        .param("nickname", nickname)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success").value(true)
                )
                .andDo(print())
                .andDo(document("check-nickname-dupl",
                        resourceDetails().tag(ApiDocTag.MEMBER.getTag())
                                .summary("닉네임 중복 체크"),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        queryParameters(
                                parameterWithName("nickname").description("닉네임, 2~8글자 한글/영문/숫자")
                        ),
                        responseFields(
                                fieldWithPath("success").description("사용 가능 여부"),
                                fieldWithPath("message").description("메시지")
                        )
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "1", "123456789", "특수문자@@"})
    @WithMockUser
    @DisplayName("닉네임 중복 확인 API Docs - 닉네임 validation 실패")
    void checkNickname_fail(String nickname) throws Exception {
        // given
        NicknameDuplResponse nicknameDuplResponse = NicknameDuplResponse.of(true, nickname);
        when(memberProfileService.nicknameDuplCheck(nickname))
                .thenReturn(nicknameDuplResponse);
        when(jwtProperties.getTokenType()).thenReturn("Bearer");

        // expected
        mockMvc.perform(get("/members/check-nickname")
                        .param("nickname", nickname)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}")
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name())
                )
                .andDo(print())
                .andDo(document("check-nickname-dupl-fail",
                        resourceDetails().tag(ApiDocTag.MEMBER.getTag())
                                .summary("닉네임 중복 체크 실패"),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        queryParameters(
                                parameterWithName("nickname").description("닉네임, 2~8글자 한글/영문/숫자")
                        ),
                        responseFields(
                                ApiDocUtils.buildDetailErrorResponseFields()
                        )
                ));
    }

}