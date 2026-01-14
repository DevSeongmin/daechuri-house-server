	package org.daechurihouse.core.security.filter;

	import java.io.IOException;

	import org.daechurihouse.core.exception.ErrorCode;
	import org.daechurihouse.core.response.FilterResponseUtil;
	import org.daechurihouse.core.security.jwt.JwtUtil;
	import org.daechurihouse.domain.auth.service.RefreshTokenService;
	import org.daechurihouse.domain.user.service.UserService;
	import org.springframework.http.HttpStatus;
	import org.springframework.web.filter.GenericFilterBean;

	import io.jsonwebtoken.Claims;
	import io.jsonwebtoken.ExpiredJwtException;
	import io.jsonwebtoken.JwtException;
	import io.jsonwebtoken.MalformedJwtException;
	import io.jsonwebtoken.security.SignatureException;
	import jakarta.servlet.FilterChain;
	import jakarta.servlet.ServletException;
	import jakarta.servlet.ServletRequest;
	import jakarta.servlet.ServletResponse;
	import jakarta.servlet.http.Cookie;
	import jakarta.servlet.http.HttpServletRequest;
	import jakarta.servlet.http.HttpServletResponse;
	import lombok.RequiredArgsConstructor;

	@RequiredArgsConstructor
	public class CustomSignOutFilter extends GenericFilterBean {

		private final JwtUtil jwtUtil;
		private final RefreshTokenService refreshTokenService;

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws
			IOException,
			ServletException {

			doFilter((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
		}

		private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
			IOException,
			ServletException {

			String requestUri = request.getRequestURI();
			if (!requestUri.matches("^/api/v1/auth/sign-out$")) {

				filterChain.doFilter(request, response);
				return;
			}

			String requestMethod = request.getMethod();
			if (!requestMethod.equals("POST")) {

				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.METHOD_NOT_ALLOWED);
				return;
			}

			String refreshToken = null;
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {

				if (cookie.getName().equals("refresh")) {

					refreshToken = cookie.getValue();
				}
			}

			if (refreshToken == null) {
				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
				return;
			}

			Claims claims;
			try {
				claims = jwtUtil.validateToken(refreshToken);
			} catch (ExpiredJwtException e) {
				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.EXPIRED_REFRESH_TOKEN);
				return;
			} catch (MalformedJwtException e) {
				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.MALFORMED_TOKEN);
				return;
			} catch (SignatureException e) {
				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
				return;
			} catch (JwtException e) {
				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.SERVER_ERROR);
				return;
			}

			String username = jwtUtil.getUsername(claims);
			String category = jwtUtil.getCategory(claims);
			if (!category.equals("refresh")) {
				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
				return;
			}

			Boolean isExist = refreshTokenService.existsByRefresh(username, refreshToken);
			if (!isExist) {
				FilterResponseUtil.setProblemDetailResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
				return;
			}

			refreshTokenService.deleteRefresh(username);

			Cookie cookie = new Cookie("refresh", null);
			cookie.setMaxAge(0);
			cookie.setPath("/");

			response.addCookie(cookie);
			response.setStatus(HttpStatus.OK.value());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
		}

	}
