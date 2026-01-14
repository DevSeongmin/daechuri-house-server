package org.daechurihouse.domain.user.service;

import java.util.List;

import org.daechurihouse.core.exception.BaseException;
import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.infra.notification.AlarmSender;
import org.daechurihouse.domain.alarm.Alarm;
import org.daechurihouse.domain.user.User;
import org.daechurihouse.domain.user.dto.UserSignupRequest;
import org.daechurihouse.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AlarmSender alarmSender;

	@Transactional
	public User register(UserSignupRequest request) {
		checkDuplicateUsername(request.username());

		User user = User.register(request, bCryptPasswordEncoder);

		alarmSender.send(Alarm.createHelloAlarm(request));

		return userRepository.save(user);
	}

	private void checkDuplicateUsername(String username) {
		if (userRepository.findByUsername(username).isPresent()) {
			throw new BaseException(ErrorCode.USER_NAME_DUPLICATED);
		}
	}

	public List<User> findUserInfos() {
		return userRepository.findUserInfos();
	}

	@Transactional
	public void approve(Long userId) {
		User user = find(userId);

		user.approve();

		userRepository.save(user);
	}

	public User find(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new BaseException(ErrorCode.USER_NOT_FOUND)
		);
	}
}
