package org.daechurihouse.core.security.filter;

import java.io.IOException;

import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.response.FilterResponseUtil;
import org.daechurihouse.core.security.CustomUserDetails;
import org.daechurihouse.core.security.jwt.JwtProperties;
import org.daechurihouse.core.security.jwt.JwtUtil;
import org.daechurihouse.domain.user.RoleType;
import org.daechurihouse.domain.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = authorization.split(" ")[1];

		// 토큰이 없다면 다음 필터로 넘김
		if (accessToken == null) {
			return;
		}

		Claims claims;
		try {
			claims = jwtUtil.validateToken(accessToken);
		} catch (ExpiredJwtException e) {
			FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
			return;
		} catch (MalformedJwtException e) {
			FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.MALFORMED_TOKEN);
			return;
		} catch (SignatureException e) {
			FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
			return;
		} catch (JwtException e) {
			FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.SERVER_ERROR);
			return;
		}

		String category = jwtUtil.getCategory(claims);
		if (category == null || !category.equals(jwtProperties.ACCESS_TOKEN_TYPE)) {
			FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
			return;
		}

		String username = jwtUtil.getUsername(claims);
		String role = jwtUtil.getRole(claims);

		User user = User.of(username, RoleType.valueOf(role));

		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
			customUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);

	}
}
