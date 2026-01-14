package org.daechurihouse.core.security.helper;

import org.daechurihouse.core.exception.BaseException;
import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.security.jwt.JwtProperties;
import org.daechurihouse.core.security.jwt.JwtUtil;
import org.daechurihouse.domain.auth.service.RefreshTokenService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthTokenHelper {

	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;
	private final JwtProperties jwtProperties;

	public String getRefreshTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refresh".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		throw new BaseException(ErrorCode.EMPTY_REFRESH_TOKEN);
	}

	public Claims validateAndGetClaims(String refresh) {
		Claims claims = jwtUtil.validateToken(refresh);
		validateRefreshToken(claims, refresh);
		return claims;
	}

	public void validateRefreshToken(Claims claims, String refresh) {
		String category = jwtUtil.getCategory(claims);
		String username = jwtUtil.getUsername(claims);

		if (!jwtProperties.REFRESH_TOKEN_TYPE.equals(category)) {
			throw new BaseException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
		if (!refreshTokenService.existsByRefresh(username, refresh)) {
			throw new BaseException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}
}

