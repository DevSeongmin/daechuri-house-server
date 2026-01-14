package org.daechurihouse.core.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum
ErrorCode {

	// Common
	SERVER_ERROR("SERVER_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),
	PATH_NOT_FOUND("PATH_404", HttpStatus.NOT_FOUND, "요청하신 경로를 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED("METHOD_405", HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),
	INVALID_TYPE_VALUE("TYPE_400", HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다."),

	// Validation (입력 유효성, 누락 등)
	INVALID_INPUT("INPUT_400", HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
	REQUIRED_USERNAME("INPUT_400_1", HttpStatus.BAD_REQUEST, "username은 필수 입력 항목입니다."),
	INVALID_METHOD_ARGUMENT("INPUT_400_2", HttpStatus.BAD_REQUEST, "잘못된 입력값이 포함되어 있습니다."),
	MISSING_REQUEST_PARAMETER("REQ_400_1", HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),

	// Authentication, Authorization
	UNAUTHORIZED("AUTH_401", HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
	ACCESS_DENIED("AUTH_403", HttpStatus.FORBIDDEN, "권한이 없어 접근이 거부되었습니다."),
	EXPIRED_ACCESS_TOKEN("AUTH_401_1", HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다. 다시 로그인하거나 토큰을 갱신해주세요."),
	MALFORMED_TOKEN("AUTH_401_2", HttpStatus.UNAUTHORIZED, "올바르지 않은 토큰 형식입니다."),
	INVALID_ACCESS_TOKEN("AUTH_401_3", HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
	LOGIN_FAILED("AUTH_401_4", HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다. 다시 확인해 주세요."),
	INVALID_REFRESH_TOKEN("AUTH_401_5", HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다. 다시 로그인하세요."),
	EXPIRED_REFRESH_TOKEN("AUTH_401_6", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다. 다시 로그인하세요."),
	EMPTY_REFRESH_TOKEN("AUTH_401_7", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 존재하지 않습니다."),
	INVALID_USER_PASSWORD("AUTH_401_8", HttpStatus.UNAUTHORIZED, "비밀번호가 틀립니다."),
	PASSWORD_SAME_AS_OLD("AUTH_401_9", HttpStatus.BAD_REQUEST, "현재 비밀번호와 새로운 비밀번호가 같습니다."),

	// Database errors
	DUPLICATE_RESOURCE("DB_409_1", HttpStatus.CONFLICT, "중복된 리소스가 존재합니다."),
	FOREIGN_KEY_VIOLATION("DB_400_1", HttpStatus.BAD_REQUEST, "연관된 리소스가 존재하여 삭제 또는 변경할 수 없습니다."),
	CHECK_CONSTRAINT_VIOLATION("DB_400_2", HttpStatus.BAD_REQUEST, "입력값이 제약조건에 부합하지 않습니다."),
	DATABASE_ERROR("DB_500", HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),

	// User
	USER_NOT_FOUND("USER_404", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	USER_NAME_DUPLICATED("USER_400_1", HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
	PASSWORD_CONFIRMATION_MISMATCH("USER_400_2", HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
	USER_NOT_APPROVED("USER_403_1", HttpStatus.FORBIDDEN, "관리자 승인 대기 중인 계정입니다."),

	// Reservation
	RESERVATION_CONFLICT("RESERVATION_409_1", HttpStatus.CONFLICT, "이미 예약된 날짜입니다."),
	INVALID_DATE_RANGE("RESERVATION_400_1", HttpStatus.BAD_REQUEST, "시작일이 종료일보다 늦을 수 없습니다."),
	PAST_DATE_NOT_ALLOWED("RESERVATION_400_2", HttpStatus.BAD_REQUEST, "과거 날짜는 예약할 수 없습니다."),
	RESERVATION_NOT_FOUND("RESERVATION_404_1", HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
	EXCEED_MAX_NIGHTS("RESERVATION_400_3", HttpStatus.BAD_REQUEST, "최대 14박까지만 예약 가능합니다."),
	;

	private final String code;
	private final HttpStatus status;
	private final String message;
}