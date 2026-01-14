package org.daechurihouse.core.response;


import org.daechurihouse.core.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ErrorResponse {

	private final boolean success;
	private final String code;
	private final String message;
	private Object errors;

	private ErrorResponse(ErrorCode errorCode) {
		this.success = false;
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}

	private ErrorResponse(ErrorCode errorCode, Object errors) {
		this.success = false;
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
		this.errors = errors;
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode);
	}

	public static ErrorResponse of(ErrorCode errorCode, Object errors) {
		return new ErrorResponse(errorCode, errors);
	}

	// public static ErrorResponse of(ErrorCode errorCode, String message, Object extras) {
	// 	return new ErrorResponse(errorCode, message, extras);
	// }
}