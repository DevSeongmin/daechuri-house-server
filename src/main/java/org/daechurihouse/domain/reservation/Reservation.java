package org.daechurihouse.domain.reservation;

import java.time.LocalDate;

import org.daechurihouse.core.entity.AuditableEntity;
import org.daechurihouse.domain.reservation.dto.CreateReservationRequest;
import org.daechurihouse.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	indexes = {
		@Index(name = "idx_reservation_date_range", columnList = "start_date, end_date"),

		@Index(name = "idx_reservation_start_date", columnList = "start_date"),

		@Index(name = "idx_reservation_end_date", columnList = "end_date"),

		@Index(name = "idx_reservation_user_id", columnList = "user_id"),
	}
)
public class Reservation extends AuditableEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_user_id"))
	private User user;

	private String guestName;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = false)
	private Integer guestCount;

	private String memo;

	public static Reservation create(User user, CreateReservationRequest request) {
		Reservation reservation = new Reservation();

		reservation.user = user;
		reservation.guestName = user.getName();
		reservation.startDate = request.startDate();
		reservation.endDate = request.endDate();
		reservation.guestCount = request.guestCount();
		reservation.memo = request.memo();

		return reservation;
	}
}