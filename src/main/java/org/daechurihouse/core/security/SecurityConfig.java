package org.daechurihouse.core.security;

import java.util.Arrays;
import java.util.List;

import org.daechurihouse.core.security.filter.CustomSignInFilter;
import org.daechurihouse.core.security.filter.CustomSignOutFilter;
import org.daechurihouse.core.security.filter.JWTFilter;
import org.daechurihouse.core.security.handler.JwtAccessDeniedHandler;
import org.daechurihouse.core.security.jwt.JwtProperties;
import org.daechurihouse.core.security.jwt.JwtUtil;
import org.daechurihouse.domain.auth.service.RefreshTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final ObjectMapper objectMapper;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
		AuthenticationManager authenticationManager,
		RefreshTokenService refreshTokenService) throws Exception {

		CustomSignInFilter signInFilter =
			new CustomSignInFilter(authenticationManager, jwtUtil, jwtProperties, objectMapper);

		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SecurityConstants.PUBLIC_PATHS).permitAll()
				.anyRequest().authenticated())
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler))
			.addFilterBefore(new JWTFilter(jwtUtil, jwtProperties), CustomSignInFilter.class)
			.addFilterAt(signInFilter, UsernamePasswordAuthenticationFilter.class)  // 변수 사용
			.addFilterBefore(new CustomSignOutFilter(jwtUtil, refreshTokenService), LogoutFilter.class)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 허용할 Origin 패턴 (포트까지 포함)
		configuration.setAllowedOriginPatterns(List.of(
			"*"
		));

		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		configuration.setAllowedHeaders(List.of("*"));

		configuration.setAllowCredentials(true);

		configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));

		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}