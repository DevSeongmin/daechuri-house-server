package org.daechurihouse.core.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.daechurihouse.domain.auth.service.RefreshTokenService;
import org.daechurihouse.domain.user.User;
import org.daechurihouse.domain.user.repository.UserRepository;
import org.daechurihouse.domain.user.service.UserService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtProperties jwtProperties;
	private final RefreshTokenService refreshTokenService;
	private final UserRepository userRepository;

	public SecretKey getSecretKey() {
		return new SecretKeySpec(jwtProperties.SECRET_KEY.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	// 토큰에서 사용자 이름 추출
	public String getUsername(Claims claims) {
		return claims.get("username", String.class);
	}

	// 토큰에서 역할(role) 추출
	public String getRole(Claims claims) {
		return claims.get("role", String.class);
	}

	public String getCategory(Claims claims) {
		return claims.get("category", String.class);
	}

	public String createJwt(String category, String username, String role) {
		Date experationDate = new Date(
			System.currentTimeMillis() + (category.equals(jwtProperties.ACCESS_TOKEN_TYPE) ?
				jwtProperties.ACCESS_TOKEN_EXPIRY_MS :
				jwtProperties.REFRESH_TOKEN_EXPIRY_MS));

		return Jwts.builder()
			.claim("category", category)
			.claim("username", username)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(experationDate)
			.signWith(getSecretKey())
			.compact();
	}

	// 토큰 검증 및 클레임 반환
	public Claims validateToken(String token) {
		return Jwts.parser()
			.verifyWith(getSecretKey()) // 서명 검증
			.build()
			.parseSignedClaims(token) // 최신 메서드 사용
			.getPayload();
	}

	public Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

	public void addRefreshToken(String username, String refreshToken) {
		refreshTokenService.saveRefreshToken(username, refreshToken);
	}

	public User decode(String jwt) {
		Claims claims = Jwts.parser()
			.verifyWith(getSecretKey())
			.build()
			.parseSignedClaims(jwt)
			.getPayload();

		String username = claims.get("username", String.class);

		return userRepository.findByUsername(username).orElseThrow(
			() -> new RuntimeException("User not found")
		);
	}
}
