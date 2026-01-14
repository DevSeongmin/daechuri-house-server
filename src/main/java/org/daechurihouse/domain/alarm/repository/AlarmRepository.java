package org.daechurihouse.domain.alarm.repository;

import java.util.List;

import org.daechurihouse.domain.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

	List<Alarm> findTop100ByIsSendFalseOrderByIdAsc();

}
