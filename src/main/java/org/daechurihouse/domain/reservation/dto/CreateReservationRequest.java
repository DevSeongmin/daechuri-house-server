package org.daechurihouse.domain.reservation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateReservationRequest(
	LocalDate startDate,
	LocalDate endDate,

	@Min(value = 0, message = "게스트 인원은 음수일 수 없습니다.")
	@Max(value = 50, message = "게스트 인원은 최대 50명 입니다.")
	Integer guestCount,

	String memo
) {
}