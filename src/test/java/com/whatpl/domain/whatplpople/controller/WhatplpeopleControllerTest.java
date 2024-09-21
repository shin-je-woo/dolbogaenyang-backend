package com.whatpl.domain.whatplpople.controller;

import com.whatpl.ApiDocTag;
import com.whatpl.BaseSecurityWebMvcTest;
import com.whatpl.domain.whatplpople.dto.WhatplpeopleDto;
import com.whatpl.domain.whatplpople.dto.WhatplpeopleSearchCondition;
import com.whatpl.domain.whatplpople.repository.WhatplepeopleQueryRepository;
import com.whatpl.global.common.model.Career;
import com.whatpl.global.common.model.Job;
import com.whatpl.global.common.model.Skill;
import com.whatpl.global.common.model.Subject;
import com.whatpl.global.security.model.WithMockWhatplMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class WhatplpeopleControllerTest extends BaseSecurityWebMvcTest {

    @Test
    @WithMockWhatplMember
    @DisplayName("내가 찾는 피플")
    void meSearch() throws Exception {
        // given
        int page = 1;
        int size = 2;

        WhatplpeopleDto whatplpeopleDto = WhatplpeopleDto.builder()
                .memberId(1)
                .nickname("왓피플1")
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.TWO)
                .memberSkills(List.of(Skill.JAVA))
                .memberSubjects(List.of(Subject.TRAVEL))
                .build();

        List<WhatplpeopleDto> whatplpeoples = List.of(whatplpeopleDto);

        WhatplpeopleSearchCondition whatplpeopleSearchCondition = WhatplpeopleSearchCondition.builder()
                .loginMemberId(1L)
                .keyword("키워드")
                .build();

        when(whatplpeopleService.searchWhatplpeople(any(Pageable.class), any(WhatplpeopleSearchCondition.class))).thenReturn(whatplpeoples);

        mockMvc.perform(get("/me-whatplpeoples")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("keyword",whatplpeopleSearchCondition.getKeyword())
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.list[*].memberId").value(Long.valueOf(whatplpeopleDto.getMemberId()).intValue()),
                jsonPath("$.list[*].nickname").value(whatplpeopleDto.getNickname()),
                jsonPath("$.list[*].job").value(whatplpeopleDto.getJob().getValue()),
                jsonPath("$.list[*].career").value(whatplpeopleDto.getCareer().getValue()),
                jsonPath("$.list[*].memberSkills").exists(),
                jsonPath("$.list[*].memberSubjects").exists()
        ).andDo(print()).andDo(document("search-me-whatplpeople",
                resourceDetails().tag(ApiDocTag.WHATPLPEOPLE.getTag())
                        .summary("내가 찾는 피플")
                        .description("내가 찾는 피플"),
                queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("keyword").description("검색어")
                ),
                responseFields(
                        fieldWithPath("list").type(JsonFieldType.ARRAY).description("내가 찾는 피플 리스트"),
                        fieldWithPath("list[].memberId").type(JsonFieldType.NUMBER).description("내가 찾는 유저 ID"),
                        fieldWithPath("list[].nickname").type(JsonFieldType.STRING).description("내가 찾는 유저 닉네임"),
                        fieldWithPath("list[].job").type(JsonFieldType.STRING).description("내가 찾는 유저 직무"),
                        fieldWithPath("list[].career").type(JsonFieldType.STRING).description("내가 찾는 유저 경력"),
                        fieldWithPath("list[].memberSkills").type(JsonFieldType.ARRAY).description("내가 찾는 유저 기술 스택"),
                        fieldWithPath("list[].memberSubjects").type(JsonFieldType.ARRAY).description("내가 찾는 유저 도메인"),
                        fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("빈 리스트 여부")
                )
        ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("나를 찾는 피플")
    void searchMe() throws Exception {
        // given
        int page = 1;
        int size = 2;

        WhatplpeopleDto whatplpeopleDto = WhatplpeopleDto.builder()
                .memberId(2)
                .nickname("왓피플2")
                .job(Job.FRONTEND_DEVELOPER)
                .career(Career.THREE)
                .memberSkills(List.of(Skill.REACT))
                .memberSubjects(List.of(Subject.TRAVEL))
                .build();


        List<WhatplpeopleDto> whatplpeoples = List.of(whatplpeopleDto);

        WhatplpeopleSearchCondition whatplpeopleSearchCondition = WhatplpeopleSearchCondition.builder()
                .loginMemberId(1L)
                .keyword("키워드")
                .build();

        when(whatplpeopleService.searchWhatplpeopleMe(any(Pageable.class), any(WhatplpeopleSearchCondition.class))).thenReturn(whatplpeoples);

        mockMvc.perform(get("/whatplpeoples-me")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("keyword",whatplpeopleSearchCondition.getKeyword())
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.list[*].memberId").value(Long.valueOf(whatplpeopleDto.getMemberId()).intValue()),
                jsonPath("$.list[*].nickname").value(whatplpeopleDto.getNickname()),
                jsonPath("$.list[*].job").value(whatplpeopleDto.getJob().getValue()),
                jsonPath("$.list[*].career").value(whatplpeopleDto.getCareer().getValue()),
                jsonPath("$.list[*].memberSkills").exists(),
                jsonPath("$.list[*].memberSubjects").exists()
        ).andDo(print()).andDo(document("search-me-whatplpeople",
                resourceDetails().tag(ApiDocTag.WHATPLPEOPLE.getTag())
                        .summary("내가 찾는 피플")
                        .description("내가 찾는 피플"),
                queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("keyword").description("검색어")
                ),
                responseFields(
                        fieldWithPath("list").type(JsonFieldType.ARRAY).description("내가 찾는 피플 리스트"),
                        fieldWithPath("list[].memberId").type(JsonFieldType.NUMBER).description("내가 찾는 유저 ID"),
                        fieldWithPath("list[].nickname").type(JsonFieldType.STRING).description("내가 찾는 유저 닉네임"),
                        fieldWithPath("list[].job").type(JsonFieldType.STRING).description("내가 찾는 유저 직무"),
                        fieldWithPath("list[].career").type(JsonFieldType.STRING).description("내가 찾는 유저 경력"),
                        fieldWithPath("list[].memberSkills").type(JsonFieldType.ARRAY).description("내가 찾는 유저 기술 스택"),
                        fieldWithPath("list[].memberSubjects").type(JsonFieldType.ARRAY).description("내가 찾는 유저 도메인"),
                        fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("빈 리스트 여부")
                )
        ));
    }

    @Test
    @WithMockWhatplMember
    @DisplayName("모든 왓피플")
    void search() throws Exception {
        // given
        int page = 1;
        int size = 2;

        WhatplpeopleDto whatplpeopleDto = WhatplpeopleDto.builder()
                .memberId(1)
                .nickname("왓피플1")
                .job(Job.BACKEND_DEVELOPER)
                .career(Career.TWO)
                .memberSkills(List.of(Skill.JAVA))
                .memberSubjects(List.of(Subject.TRAVEL))
                .build();

        List<WhatplpeopleDto> whatplpeoples = List.of(whatplpeopleDto);

        WhatplpeopleSearchCondition whatplpeopleSearchCondition = WhatplpeopleSearchCondition.builder()
                .loginMemberId(1L)
                .keyword("키워드")
                .build();

        when(whatplpeopleService.searchAllWhatplpeopleMe(any(Pageable.class), any(WhatplpeopleSearchCondition.class))).thenReturn(whatplpeoples);

        mockMvc.perform(get("/whatplpeoples-all")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("keyword",whatplpeopleSearchCondition.getKeyword())
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON_VALUE),
                jsonPath("$.list[*].memberId").value(Long.valueOf(whatplpeopleDto.getMemberId()).intValue()),
                jsonPath("$.list[*].nickname").value(whatplpeopleDto.getNickname()),
                jsonPath("$.list[*].job").value(whatplpeopleDto.getJob().getValue()),
                jsonPath("$.list[*].career").value(whatplpeopleDto.getCareer().getValue()),
                jsonPath("$.list[*].memberSkills").exists(),
                jsonPath("$.list[*].memberSubjects").exists()
        ).andDo(print()).andDo(document("search-me-whatplpeople",
                resourceDetails().tag(ApiDocTag.WHATPLPEOPLE.getTag())
                        .summary("내가 찾는 피플")
                        .description("내가 찾는 피플"),
                queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("keyword").description("검색어")
                ),
                responseFields(
                        fieldWithPath("list").type(JsonFieldType.ARRAY).description("내가 찾는 피플 리스트"),
                        fieldWithPath("list[].memberId").type(JsonFieldType.NUMBER).description("내가 찾는 유저 ID"),
                        fieldWithPath("list[].nickname").type(JsonFieldType.STRING).description("내가 찾는 유저 닉네임"),
                        fieldWithPath("list[].job").type(JsonFieldType.STRING).description("내가 찾는 유저 직무"),
                        fieldWithPath("list[].career").type(JsonFieldType.STRING).description("내가 찾는 유저 경력"),
                        fieldWithPath("list[].memberSkills").type(JsonFieldType.ARRAY).description("내가 찾는 유저 기술 스택"),
                        fieldWithPath("list[].memberSubjects").type(JsonFieldType.ARRAY).description("내가 찾는 유저 도메인"),
                        fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("빈 리스트 여부")
                )
        ));
    }
}