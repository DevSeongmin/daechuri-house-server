package org.daechurihouse.core.security.resolver;

import org.daechurihouse.core.exception.BaseException;
import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.security.annotation.CurrentUser;
import org.daechurihouse.core.security.jwt.JwtUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtUtil jwtUtil;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(CurrentUser.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		org.springframework.web.bind.support.WebDataBinderFactory binderFactory) throws Exception {
		String token = webRequest.getHeader("Authorization");

		if (token == null || !token.startsWith("Bearer ")) {
			throw new BaseException(ErrorCode.INVALID_ACCESS_TOKEN);
		}

		String jwt = token.replace("Bearer ", "");
		return jwtUtil.decode(jwt);
	}
}