package org.daechurihouse.domain.alarm.service;

import org.daechurihouse.domain.alarm.Alarm;
import org.daechurihouse.domain.alarm.event.AlarmCreatedEvent;
import org.daechurihouse.domain.alarm.repository.AlarmRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

	private final AlarmRepository alarmRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void send(Alarm alarm) {
		alarm = alarmRepository.save(alarm);

		eventPublisher.publishEvent(new AlarmCreatedEvent(alarm.getId()));
	}
}
