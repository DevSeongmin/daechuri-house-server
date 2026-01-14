package org.daechurihouse.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
	String password,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
	String newPassword,


	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
	String confirmPassword) {
}