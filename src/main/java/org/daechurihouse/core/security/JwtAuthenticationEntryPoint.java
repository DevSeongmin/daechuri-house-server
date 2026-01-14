package org.daechurihouse.core.security;

import java.io.IOException;

import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.response.FilterResponseUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	RequestMappingInfoHandlerMapping requestMappingInfoHandlerMapping;

	public JwtAuthenticationEntryPoint(
		@Qualifier("requestMappingHandlerMapping")
		RequestMappingInfoHandlerMapping requestMappingInfoHandlerMapping
	) {
		this.requestMappingInfoHandlerMapping = requestMappingInfoHandlerMapping;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.UNAUTHORIZED);
	}
}