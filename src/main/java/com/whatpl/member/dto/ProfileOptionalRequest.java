package com.whatpl.member.dto;

import com.whatpl.global.common.domain.enums.Subject;
import com.whatpl.global.common.domain.enums.WorkTime;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class ProfileOptionalRequest {

    @Size(max = 5, message = "관심주제는 최대 5개 입력 가능합니다.")
    private Set<Subject> subjects;

    @Size(max = 3, message = "참고링크는 최대 3개 첨부 가능합니다.")
    private Set<String> references;

    private WorkTime workTime;
}
