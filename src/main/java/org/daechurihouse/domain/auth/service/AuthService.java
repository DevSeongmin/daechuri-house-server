package org.daechurihouse.domain.auth.service;

import java.util.Objects;

import org.daechurihouse.core.exception.BaseException;
import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.security.helper.AuthTokenHelper;
import org.daechurihouse.core.security.jwt.JwtProperties;
import org.daechurihouse.core.security.jwt.JwtUtil;
import org.daechurihouse.domain.auth.dto.ChangePasswordRequest;
import org.daechurihouse.domain.user.User;
import org.daechurihouse.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final RefreshTokenService refreshTokenService;
	private final AuthTokenHelper authTokenHelper;
	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;
	private final BCryptPasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Transactional
	public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
		String refresh = authTokenHelper.getRefreshTokenFromCookies(request);
		Claims claims = authTokenHelper.validateAndGetClaims(refresh);

		String username = jwtUtil.getUsername(claims);
		String role = jwtUtil.getRole(claims);

		String newAccess = jwtUtil.createJwt(jwtProperties.ACCESS_TOKEN_TYPE, username, role);
		String newRefresh = jwtUtil.createJwt(jwtProperties.REFRESH_TOKEN_TYPE, username, role);

		refreshTokenService.deleteRefresh(username);
		refreshTokenService.saveRefreshToken(username, newRefresh);

		response.setHeader("Authorization", "Bearer " + newAccess);
		response.addCookie(jwtUtil.createCookie(jwtProperties.REFRESH_TOKEN_TYPE, newRefresh));
	}

	@Transactional
	public void changePassword(User user, HttpServletRequest request, ChangePasswordRequest changePasswordRequest) {
		String refresh = authTokenHelper.getRefreshTokenFromCookies(request);
		Claims claims = authTokenHelper.validateAndGetClaims(refresh);

		if (!passwordEncoder.matches(changePasswordRequest.password(), user.getPasswordHash())) {
			throw new BaseException(ErrorCode.INVALID_USER_PASSWORD);
		}

		if (passwordEncoder.matches(changePasswordRequest.newPassword(), user.getPasswordHash())) {
			throw new BaseException(ErrorCode.PASSWORD_SAME_AS_OLD);
		}

		if (!Objects.equals(changePasswordRequest.newPassword(), changePasswordRequest.confirmPassword())) {
			throw new BaseException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
		}

		String username = jwtUtil.getUsername(claims);

		refreshTokenService.deleteRefresh(username);

		String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.newPassword());

		user.changePasswordHash(encodedNewPassword);
		userRepository.save(user);
	}
}
