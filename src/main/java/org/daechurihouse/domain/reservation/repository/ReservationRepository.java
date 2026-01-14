package org.daechurihouse.domain.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.daechurihouse.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Query("""
        SELECT r
        FROM Reservation r
        WHERE r.endDate >= :startDate
        AND r.startDate <= :endDate
        """)
	List<Reservation> findOverlappingReservations(
		LocalDate startDate,
		LocalDate endDate
	);

	@Query("""
		SELECT r
		FROM Reservation r
		WHERE (YEAR(r.startDate) = :year AND MONTH(r.startDate) = :month)
		OR (YEAR(r.endDate) = :year AND MONTH(r.endDate) = :month)
		OR (r.startDate < :monthStart AND r.endDate > :monthEnd)
		ORDER BY r.startDate ASC
		""")
	List<Reservation> findByYearAndMonth(
		int year,
		int month,
		LocalDate monthStart,
		LocalDate monthEnd
	);

	List<Reservation> findByUserIdOrderByIdAsc(Long userId);

	@Query("""
		SELECT r
		FROM Reservation r
		LEFT JOIN FETCH r.user
		WHERE r.id = :reservationId
		""")
	Optional<Reservation> findByIdWithUser(Long reservationId);
}