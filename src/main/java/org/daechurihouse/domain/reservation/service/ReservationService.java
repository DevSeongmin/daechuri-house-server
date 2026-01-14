package org.daechurihouse.domain.reservation.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import org.daechurihouse.core.exception.BaseException;
import org.daechurihouse.core.exception.ErrorCode;
import org.daechurihouse.domain.alarm.Alarm;
import org.daechurihouse.domain.alarm.service.AlarmService;
import org.daechurihouse.domain.reservation.Reservation;
import org.daechurihouse.domain.reservation.dto.CreateReservationRequest;
import org.daechurihouse.domain.reservation.repository.ReservationRepository;
import org.daechurihouse.domain.user.RoleType;
import org.daechurihouse.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

	private final long MAX_NIGHTS = 14L;
	private final ReservationRepository reservationRepository;
	private final AlarmService alarmService;

	@Transactional
	public void createReservation(User user, CreateReservationRequest request) {

		LocalDate startDate = request.startDate();
		LocalDate endDate = request.endDate();

		validateDates(startDate, endDate);
		validateNoOverlap(startDate, endDate);

		Reservation reservation = Reservation.create(user, request);

		alarmService.send(Alarm.createReservationAlarm(user.getName(), request));

		reservationRepository.save(reservation);
	}

	private void validateDates(LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new BaseException(ErrorCode.INVALID_DATE_RANGE);
		}

		if (startDate.isBefore(LocalDate.now())) {
			throw new BaseException(ErrorCode.PAST_DATE_NOT_ALLOWED);
		}

		long nights = ChronoUnit.DAYS.between(startDate, endDate);
		if (nights > MAX_NIGHTS) {
			throw new BaseException(ErrorCode.EXCEED_MAX_NIGHTS);
		}
	}

	private void validateNoOverlap(LocalDate startDate, LocalDate endDate) {
		List<Reservation> overlapping =
			reservationRepository.findOverlappingReservations(startDate, endDate);

		if (!overlapping.isEmpty()) {
			throw new BaseException(ErrorCode.RESERVATION_CONFLICT);
		}
	}


	public List<Reservation> getReservationsByYearMonth(int year, int month) {
		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate monthStart = yearMonth.atDay(1);
		LocalDate monthEnd = yearMonth.atEndOfMonth();

		return reservationRepository.findByYearAndMonth(
			year, month, monthStart, monthEnd
		);
	}

	public List<Reservation> getReservations(User user) {
		return reservationRepository.findByUserIdOrderByIdAsc(user.getId());
	}

	/*
	TODO

수정 사항이 있어 달력 UI에서는 최대 14박 까지만 예약이 가능하도록 수정해줘


또한 예약을 삭제할 수 있는 기능이 추가 되었어

DELETE baseUrl/api/v1/reservations/reservationId

	 */
	@Transactional
	public void remove(User user, Long reservationId) {
		Reservation reservation = reservationRepository.findByIdWithUser(reservationId)
			.orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

		if (!(user.getRole() == RoleType.ROLE_ADMIN)) {
			checkReservationOwner(user, reservation);
		}

		reservationRepository.delete(reservation);
	}

	private static void checkReservationOwner(User user, Reservation reservation) {
		if (!Objects.equals(reservation.getUser(), user)) {
			throw new BaseException(ErrorCode.RESERVATION_NOT_FOUND);
		}
	}
}
