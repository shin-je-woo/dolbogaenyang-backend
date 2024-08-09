package com.whatpl.global.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Subject implements WhatplGlobalDomain {

    INTERIOR("홈/인테리어"),
    SOCIAL_MEDIA("소셜 미디어"),
    PARENTING("출산/육아"),
    MAP("지도/네비게이션"),
    MOBILITY("모빌리티"),
    MEDICAL("의료"),
    MUSIC("음악/오디오"),
    ART("예술/디자인"),
    TRAVEL("여행 및 지역정보"),
    ENTERTAINMENT("엔터테인먼트/게임"),
    SHOPPING("쇼핑"),
    BEAUTY("뷰티/패션"),
    CARTOON("만화"),
    BOOKS("도서"),
    DATING("데이트/결혼"),
    NEWS("뉴스/잡지"),
    WEATHER("날씨"),
    FINANCE("금융/핀테크"),
    EDUCATION("교육"),
    RELIGION("종교"),
    HEALTH("건강/운동/스포츠");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Subject from(String value) {
        return Arrays.stream(Subject.values())
                .filter(subject -> subject.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.SUBJECT_NOT_VALID));
    }
}
