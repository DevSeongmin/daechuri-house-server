package org.daechurihouse.domain.user.controller.dto;

import java.time.LocalDateTime;

import org.daechurihouse.domain.user.User;
import org.daechurihouse.domain.user.UserStatus;

public record UserInfosResponse(
	Long userId,
	String username,
	String name,
	UserStatus status,
	String helloMessage,
	LocalDateTime createdAt
) {
	public static UserInfosResponse from(User user) {
		return new UserInfosResponse(user.getId(), user.getUsername(), user.getName(), user.getStatus(),user.getHelloMessage(), user.getCreatedAt());
	}
}
