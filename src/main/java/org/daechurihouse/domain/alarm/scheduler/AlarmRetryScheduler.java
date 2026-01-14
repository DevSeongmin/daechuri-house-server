package org.daechurihouse.domain.alarm.scheduler;

import java.util.List;

import org.daechurihouse.core.infra.notification.AlarmSender;
import org.daechurihouse.domain.alarm.Alarm;
import org.daechurihouse.domain.alarm.repository.AlarmRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmRetryScheduler {

	private final AlarmRepository alarmRepository;
	private final AlarmSender alarmSender;

	@Scheduled(fixedDelay = 60_000)
	@Transactional
	public void resendFailedAlarms() {
		List<Alarm> alarms = alarmRepository.findTop100ByIsSendFalseOrderByIdAsc();
		if (alarms.isEmpty()) {
			return;
		}

		for (Alarm alarm : alarms) {
			boolean success = alarmSender.send(alarm);
			if (success) {
				alarm.markSent();
			}
		}
	}
}