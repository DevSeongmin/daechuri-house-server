package org.daechurihouse.core.infra.notification;

import org.daechurihouse.domain.alarm.Alarm;

public interface AlarmSender {

	boolean send(Alarm alarm);

}
