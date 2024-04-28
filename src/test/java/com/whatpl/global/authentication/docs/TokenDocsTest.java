package com.whatpl.global.authentication.docs;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.global.jwt.JwtResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class TokenDocsTest extends BaseSecurityWebMvcTest {

    @Test
    @DisplayName("토큰 재발급 API Docs")
    void reIssue() throws Exception {
        // given
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        when(jwtService.reIssueToken(any()))
                .thenReturn(jwtResponse);

        // expected
        mockMvc.perform(post("/token").param("refreshToken", "valid-refreshToken")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("refreshToken=valid-refreshToken"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accessToken").value(jwtResponse.getAccessToken()),
                        jsonPath("$.refreshToken").value(jwtResponse.getRefreshToken())
                )
                .andDo(print())
                .andDo(document("reissue-token",
                        resourceDetails().tag(ApiDocTag.AUTHENTICATION.getTag())
                                .summary("토큰 재발급")
                                .description("""
                                        토큰을 재발급합니다.
                                                                                                
                                        RTR(Refresh Token Rotation) 방식으로 accessToken과 refreshToken을 모두 재발급합니다. (기존의 refreshToken은 삭제됩니다.)
                                        """),
                        formParameters(
                                parameterWithName("refreshToken").description("refreshToken - 임의의 값 (UUID)")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("accessToken - JWT"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("refreshToken - 임의의 값 (UUID)")
                        )
                ));
    }
}
