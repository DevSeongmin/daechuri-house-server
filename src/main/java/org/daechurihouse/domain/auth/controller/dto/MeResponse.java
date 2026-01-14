package org.daechurihouse.domain.auth.controller.dto;

import java.time.LocalDateTime;

import org.daechurihouse.domain.user.RoleType;
import org.daechurihouse.domain.user.User;

public record MeResponse(
	Long userId,
	String userName,
	RoleType roleType,
	LocalDateTime createdAt) {

	public static MeResponse of(User user) {
		return new MeResponse(user.getId(), user.getUsername(), user.getRole(), user.getCreatedAt());
	}
}
