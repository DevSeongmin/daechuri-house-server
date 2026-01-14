package org.daechurihouse.domain.auth.service;

import java.util.Objects;

import org.daechurihouse.domain.user.User;
import org.daechurihouse.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final UserRepository userRepository;

	@Transactional
	public void saveRefreshToken(String username, String refreshToken) {
		User user = userRepository.findByUsername(username).orElseThrow();
		user.setRefreshToken(refreshToken);
		userRepository.save(user);
	}

	public Boolean existsByRefresh(String username, String refreshToken) {
		return Objects.equals(
			userRepository.findByUsername(username).orElseThrow().getRefreshToken(),
			refreshToken
		);
	}

	@Transactional
	public void deleteRefresh(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();

		user.setRefreshToken(null);

		userRepository.save(user);
	}
}
