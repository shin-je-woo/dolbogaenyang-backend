package com.whatpl.member.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.ApiDocUtils;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.security.model.WithMockWhatplMember;
import com.whatpl.global.common.domain.enums.Career;
import com.whatpl.global.common.domain.enums.Job;
import com.whatpl.global.common.domain.enums.Skill;
import com.whatpl.member.dto.NicknameDuplResponse;
import com.whatpl.member.dto.ProfileOptionalRequest;
import com.whatpl.member.dto.ProfileRequiredRequest;
import com.whatpl.member.model.ProfileOptionalRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("사용 가능 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
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

    @Test
    @WithMockWhatplMember(2L)
    @DisplayName("프로필 필수 정보 입력 API Docs")
    void required() throws Exception {
        // given
        ProfileRequiredRequest request = ProfileRequiredRequest.builder()
                .nickname("닉네임")
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.THREE)
                .skills(Set.of(Skill.JAVA, Skill.MYSQL))
                .profileOpen(true)
                .build();
        doNothing().when(memberProfileService).updateRequiredProfile(any(ProfileRequiredRequest.class), any(Long.class));

        // expected
        mockMvc.perform(post("/members/required")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("required",
                        resourceDetails().tag(ApiDocTag.MEMBER.getTag())
                                .summary("프로필 필수 정보 입력"),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("career").type(JsonFieldType.STRING).description("경력"),
                                fieldWithPath("job").type(JsonFieldType.STRING).description("직무"),
                                fieldWithPath("skills").type(JsonFieldType.ARRAY).description("기술스택"),
                                fieldWithPath("profileOpen").type(JsonFieldType.BOOLEAN).description("프로필 공개 여부 [기본값=false]").optional()
                        )
                ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("프로필 기본 정보 입력 API Docs")
    void optional() throws Exception {
        // given
        ProfileOptionalRequest info = ProfileOptionalRequestFixture.create();
        String infoJson = objectMapper.writeValueAsString(info);
        MockMultipartFile infoRequest = new MockMultipartFile("info", "", MediaType.APPLICATION_JSON_VALUE, infoJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile mockMultipartFile1 = createMockMultipartFile("portfolios", "cat.jpg", MediaType.IMAGE_JPEG_VALUE);
        MockMultipartFile mockMultipartFile2 = createMockMultipartFile("portfolios", "dummy.pdf", MediaType.IMAGE_PNG_VALUE);
        doNothing().when(memberProfileService).updateOptionalProfile(any(ProfileOptionalRequest.class), anyList(), any(Long.class));

        // expected
        mockMvc.perform(multipart("/members/optional")
                        .file(infoRequest)
                        .file(mockMultipartFile1)
                        .file(mockMultipartFile2)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {AccessToken}"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("optional",
                        resourceDetails()
                                .tag(ApiDocTag.MEMBER.getTag())
                                .summary("프로필 선택 정보 입력")
                                .description("""
                                        프로필 선택 정보 입력
                                                                                
                                        현재 사용 플러그인(ePages/restdocs-api-spec)이 multipart/form-data 문서화가 지원되지 않습니다. (try it out 불가능)

                                        아래 형식을 참고하여 요청하면 됩니다.
                                        
                                        <h2> JSON Part </h2>
                                        | name   | Content-Type           | Description          |
                                        |--------|------------------------| ---------------------|
                                        | info   | application/json       | Member 프로필 정보     |
                                                                                
                                        <h2> JSON Field Part - info </h2>
                                        | key         | Type       | Description          |
                                        |-------------|------------| ---------------------|
                                        | subjects    | List       | 관심주제               |
                                        | references  | List       | 참고링크               |
                                        | workTime    | String     | 작업시간               |
                                                                                
                                        <h2> File Part </h2>
                                        | name         | filename  |Content-Type                 | Description            |
                                        |--------------|-----------|-----------------------------| -----------------------|
                                        | portfolios   | 파일명     | multipart/form-data         | 포트폴리오 첨부파일        |
                                        """),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken"),
                                headerWithName(CONTENT_TYPE).description(MULTIPART_FORM_DATA_VALUE)
                        ), requestParts(
                                partWithName("info").description("선택정보"),
                                partWithName("portfolios").description("포트폴리오")
                        ),
                        requestPartFields("info",
                                fieldWithPath("subjects").description("관심주제"),
                                fieldWithPath("references").description("참고링크"),
                                fieldWithPath("workTime").description("작업시간"))

                ));
    }
}