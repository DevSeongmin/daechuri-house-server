package org.daechurihouse.core.response;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class SuccessResponse<T> {
	private final static SuccessResponse<Void> EMPTY = new SuccessResponse<>();

	private final boolean success;
	private T data;

	private SuccessResponse() {
		this.success = true;
	}

	private SuccessResponse(HttpStatus httpStatus) {
		this.success = true;
	}

	private SuccessResponse(T data) {
		this.success = true;
		this.data = data;
	}
	public static SuccessResponse<Void> ok() {
		return EMPTY;
	}

	public static <T> SuccessResponse<T> of(T data) {
		return new SuccessResponse<>(data);
	}
}
