package org.daechurihouse.core.exception.advice;

import org.daechurihouse.core.exception.BaseException;
import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.core.response.CustomValidationError;
import org.daechurihouse.core.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomExceptionAdvice {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("예기치 못한 에러: \n class : {} \n message: {}", e.getClass(), e.getMessage());

		ErrorCode errorCode = ErrorCode.SERVER_ERROR;

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode, e.getMessage()));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> baseException(BaseException e) {

		ErrorCode errorCode = e.getErrorCode();

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException() {

		ErrorCode errorCode = ErrorCode.PATH_NOT_FOUND;

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException() {

		ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException() {

		ErrorCode errorCode = ErrorCode.INVALID_TYPE_VALUE;

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {

		CustomValidationError customValidationError = new CustomValidationError(e.getBindingResult());
		ErrorCode errorCode = ErrorCode.INVALID_METHOD_ARGUMENT;

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.of(errorCode, customValidationError.getErrors()));
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException() {

		ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException() {

		ErrorCode errorCode = ErrorCode.MISSING_REQUEST_PARAMETER;

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}
}
