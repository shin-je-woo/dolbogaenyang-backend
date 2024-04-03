package com.whatpl;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApiDocUtils {

    private ApiDocUtils() {
        throw new IllegalStateException("This is utility class!");
    }

    public static List<FieldDescriptor> buildDetailErrorResponseFields() {
        return List.of(
                fieldWithPath("code").type(JsonFieldType.STRING)
                        .description("에러코드"),
                fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("상태코드"),
                fieldWithPath("message").type(JsonFieldType.STRING)
                        .description("에러 메시지"),
                fieldWithPath("detailErrors").type(JsonFieldType.ARRAY)
                        .description("에러 상세 메시지"),
                fieldWithPath("detailErrors[].field").type(JsonFieldType.STRING)
                        .description("요청 필드"),
                fieldWithPath("detailErrors[].value").type(JsonFieldType.STRING)
                        .description("요청 값"),
                fieldWithPath("detailErrors[].reason").type(JsonFieldType.STRING)
                        .description("에러 사유")
        );
    }
}
