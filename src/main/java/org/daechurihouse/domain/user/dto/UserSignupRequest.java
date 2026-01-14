package org.daechurihouse.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignupRequest(

	@NotBlank(message = "아이디는 필수입니다.")
	@Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
	String username,

	@NotBlank(message = "이름은 필수 입니다.")
	@Size(min = 2, max = 10, message = "이름은 2~10자 여야 합니다.")
	String name,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
	String password,

	@NotBlank(message = "가입 인사를 남겨주세요")
	String helloMessage
	) {
}
