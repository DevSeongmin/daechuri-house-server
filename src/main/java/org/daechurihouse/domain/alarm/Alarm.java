package org.daechurihouse.domain.alarm;

import org.daechurihouse.core.entity.AuditableEntity;
import org.daechurihouse.domain.reservation.dto.CreateReservationRequest;
import org.daechurihouse.domain.user.dto.UserSignupRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends AuditableEntity {

	@Column(columnDefinition = "text")
	private String alarmMessage;

	@Enumerated(EnumType.STRING)
	private AlarmType type;

	private boolean isSend = false;


	public static Alarm createHelloAlarm(UserSignupRequest request) {
		Alarm alarm = new Alarm();
		alarm.type = AlarmType.SIGN_UP;

		alarm.alarmMessage = String.format("""
            *새로운 가입자 정보*
            
            *아이디:* `%s`
            *이름:* %s
            *가입 인사:*
            > %s
            """,
			request.username(),
			request.name(),
			request.helloMessage()
		);

		return alarm;
	}

	public static Alarm createReservationAlarm(
		String userName,
		CreateReservationRequest request) {

		Alarm alarm = new Alarm();
		alarm.type = AlarmType.RESERVATION;

		alarm.alarmMessage = String.format("""
            *예약 상세 정보*
            
            *예약자:* %s
            *인원:* %d명
            *기간:* `%s` ~ `%s`
            *메모:*
            > %s
            """,
			userName,
			request.guestCount(),
			request.startDate(),
			request.endDate(),
			request.memo() != null ? request.memo() : "없음"
		);
		return alarm;
	}

	public void markSent() {
		this.isSend = true;
	}
}
