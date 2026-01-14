package org.daechurihouse.domain.reservation.controller;

import java.util.List;

import org.daechurihouse.core.response.SuccessResponse;
import org.daechurihouse.core.security.annotation.CurrentUser;
import org.daechurihouse.domain.reservation.Reservation;
import org.daechurihouse.domain.reservation.controller.dto.ReservationResponse;
import org.daechurihouse.domain.reservation.dto.CreateReservationRequest;
import org.daechurihouse.domain.reservation.service.ReservationService;
import org.daechurihouse.domain.user.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping
	public SuccessResponse<Void> createReservation(
		@CurrentUser User user,
		@RequestBody CreateReservationRequest request) {

		reservationService.createReservation(user, request);

		return SuccessResponse.ok();
	}

	@GetMapping("/calendar")
	public SuccessResponse<List<ReservationResponse>> getCalendarReservations(
		@RequestParam int year,
		@RequestParam int month) {

		List<Reservation> reservations =
			reservationService.getReservationsByYearMonth(year, month);

		List<ReservationResponse> response = reservations.stream()
			.map(ReservationResponse::from)
			.toList();

		return SuccessResponse.of(response);
	}

	@GetMapping("")
	public SuccessResponse<List<ReservationResponse>> getReservations(
		@CurrentUser User user) {

		List<Reservation> reservations =
			reservationService.getReservations(user);

		List<ReservationResponse> response = reservations.stream()
			.map(ReservationResponse::from)
			.toList();

		return SuccessResponse.of(response);
	}

	@DeleteMapping("/{reservationId}")
	public SuccessResponse<Void> removeReservation(@CurrentUser User user, @PathVariable Long reservationId) {

		reservationService.remove(user, reservationId);

		return SuccessResponse.ok();
	}
}
