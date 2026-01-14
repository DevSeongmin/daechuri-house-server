package org.daechurihouse.domain.user.controller;

import java.util.List;

import org.daechurihouse.core.response.SuccessResponse;
import org.daechurihouse.domain.user.User;
import org.daechurihouse.domain.user.controller.dto.UserInfosResponse;
import org.daechurihouse.domain.user.dto.UserSignupRequest;
import org.daechurihouse.domain.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/sign-up")
	public SuccessResponse<Void> register(@Valid @RequestBody UserSignupRequest request) {

		userService.register(request);

		return SuccessResponse.ok();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("")
	public SuccessResponse<List<UserInfosResponse>> getUserInfos() {

		List<User> userInfos = userService.findUserInfos();

		List<UserInfosResponse> response = userInfos.stream().map(UserInfosResponse::from).toList();

		return SuccessResponse.of(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{userId}/approve")
	public SuccessResponse<Void> approve(@PathVariable Long userId) {

		userService.approve(userId);

		return SuccessResponse.ok();
	}
}
