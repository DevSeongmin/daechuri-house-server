package org.daechurihouse.domain.auth.controller;

import org.daechurihouse.core.response.SuccessResponse;
import org.daechurihouse.core.security.annotation.CurrentUser;
import org.daechurihouse.domain.auth.dto.ChangePasswordRequest;
import org.daechurihouse.domain.auth.controller.dto.MeResponse;
import org.daechurihouse.domain.auth.service.AuthService;
import org.daechurihouse.domain.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthController {

	private final AuthService authService;


	@PostMapping("/reissue")
	public SuccessResponse<Void> reissue(HttpServletRequest request, HttpServletResponse response) {

		authService.reissueToken(request, response);

		return SuccessResponse.ok();
	}

	@PatchMapping("/change-password")
	public SuccessResponse<Void> changePassword(@CurrentUser User user,
		HttpServletRequest request,
		@Valid @RequestBody ChangePasswordRequest changePasswordrequest) {

		authService.changePassword(user, request, changePasswordrequest);

		return SuccessResponse.ok();
	}

	@GetMapping("/me")
	public SuccessResponse<MeResponse> me(@CurrentUser User user) {

		return SuccessResponse.of(MeResponse.of(user));
	}
}