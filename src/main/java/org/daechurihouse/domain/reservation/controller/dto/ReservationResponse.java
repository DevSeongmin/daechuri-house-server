package org.daechurihouse.domain.reservation.controller.dto;

import java.time.LocalDate;

import org.daechurihouse.domain.reservation.Reservation;

public record ReservationResponse(
    Long id,
    String guestName,
    LocalDate startDate,
    LocalDate endDate,
    Integer guestCount,
    String memo
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getGuestName(),
            reservation.getStartDate(),
            reservation.getEndDate(),
            reservation.getGuestCount(),
            reservation.getMemo()
        );
    }
}
