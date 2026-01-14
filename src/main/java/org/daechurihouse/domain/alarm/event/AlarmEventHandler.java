package org.daechurihouse.domain.alarm.event;

import org.daechurihouse.core.infra.notification.AlarmSender;
import org.daechurihouse.domain.alarm.Alarm;
import org.daechurihouse.domain.alarm.repository.AlarmRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmEventHandler {

	private final AlarmRepository alarmRepository;
	private final AlarmSender alarmSender;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void onAlarmCreated(AlarmCreatedEvent event) {
		Alarm alarm = alarmRepository.findById(event.alarmId())
			.orElseThrow(() -> new IllegalStateException("Alarm not found: " + event.alarmId()));

		if (alarm.isSend()) {
			return;
		}

		boolean success = alarmSender.send(alarm);
		if (success) {
			alarm.markSent();
			alarmRepository.save(alarm);
		} else {
			log.error("Failed to send alarm: {}", alarm.getId());
		}
	}
}
