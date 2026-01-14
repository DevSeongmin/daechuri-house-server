package org.daechurihouse.core.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.response.FilterResponseUtil;
import org.daechurihouse.core.security.jwt.JwtProperties;
import org.daechurihouse.core.security.jwt.JwtUtil;
import org.daechurihouse.domain.user.dto.SignInRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomSignInFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;
	private final ObjectMapper objectMapper;

	public CustomSignInFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, JwtProperties jwtProperties,
		ObjectMapper objectMapper) {

		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.jwtProperties = jwtProperties;
		this.objectMapper = objectMapper;
		setFilterProcessesUrl("/api/v1/auth/sign-in");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (request.getRequestURI().equals("/api/v1/auth/sign-in")
			&& !"POST".equals(request.getMethod())) {
			FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.METHOD_NOT_ALLOWED);
			return;
		}

		super.doFilter(req, res, chain);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		SignInRequest signInRequest;

		try {
			ServletInputStream inputStream = request.getInputStream();
			String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
			signInRequest = objectMapper.readValue(messageBody, SignInRequest.class);

		} catch (IOException e) {
			throw new BadCredentialsException("요청이 올바르지 않습니다.");
		}

		String username = signInRequest.username();
		String password = signInRequest.password();

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
			null);

		try {
			return authenticationManager.authenticate(authToken);
		} catch (InternalAuthenticationServiceException e) {
			throw new BadCredentialsException("아이디 또는 비밀번호를 확인해주세요.");
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException, ServletException {
		String username = authentication.getName();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String accessToken = jwtUtil.createJwt(jwtProperties.ACCESS_TOKEN_TYPE, username, role);
		String refreshToken = jwtUtil.createJwt(jwtProperties.REFRESH_TOKEN_TYPE, username, role);

		jwtUtil.addRefreshToken(username, refreshToken);

		response.setHeader("Authorization", "Bearer " + accessToken);
		response.addCookie(jwtUtil.createCookie(jwtProperties.REFRESH_TOKEN_TYPE, refreshToken));
		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		if (failed instanceof DisabledException) {
			FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.USER_NOT_APPROVED);
			return;
		}

		FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.LOGIN_FAILED);
	}
}