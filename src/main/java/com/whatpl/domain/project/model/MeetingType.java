package com.whatpl.domain.project.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MeetingType {

    ONLINE("online"),
    OFFLINE("offline"),
    ANY("any");

    @JsonValue
    private final String value;

    @JsonCreator
    public static MeetingType from(String value) {
        return Arrays.stream(MeetingType.values())
                .filter(meetingType -> meetingType.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.MEETING_TYPE_NOT_VALID));
    }
}
