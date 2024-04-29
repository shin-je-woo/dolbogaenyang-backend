package com.whatpl.global.web.resolver;

import com.whatpl.global.exception.BizException;
import com.whatpl.global.exception.ErrorCode;
import com.whatpl.global.upload.enums.DefaultImage;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.data.util.CastUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Optional;

public class DefaultImageArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(DefaultImage.class);
    }

    @Override
    public Object resolveArgument(@Nonnull MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = CastUtils.cast(webRequest.getNativeRequest());
        String type = Optional.ofNullable(request.getParameter("type"))
                .orElseThrow(() -> new BizException(ErrorCode.REQUIRED_PARAMETER_MISSING));
        return Arrays.stream(DefaultImage.values())
                .filter(defaultImg -> defaultImg.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.HTTP_MESSAGE_NOT_READABLE));
    }
}
