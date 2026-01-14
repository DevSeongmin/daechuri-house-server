package org.daechurihouse.core.exception;

import org.springframework.validation.Errors;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
	private final ErrorCode errorCode;
	private Errors errors;

	public BaseException(Throwable cause) {
		super(cause);
		this.errorCode = ErrorCode.SERVER_ERROR;
	}

	public BaseException(ErrorCode errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	public BaseException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public BaseException(ErrorCode errorCode, Errors errors) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.errors = errors;
	}
}
