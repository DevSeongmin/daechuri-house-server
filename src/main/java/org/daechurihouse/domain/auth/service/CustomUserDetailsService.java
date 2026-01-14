package org.daechurihouse.domain.auth.service;

import org.daechurihouse.core.exception.BaseException;
import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.security.CustomUserDetails;
import org.daechurihouse.domain.user.User;
import org.daechurihouse.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username).orElseThrow(
			() -> new BaseException(ErrorCode.USER_NOT_FOUND)
		);

		return new CustomUserDetails(user);
	}
}