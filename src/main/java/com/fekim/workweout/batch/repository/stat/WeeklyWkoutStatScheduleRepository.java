package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.stat.entity.WeeklyWkoutStatSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyWkoutStatScheduleRepository extends JpaRepository<WeeklyWkoutStatSchedule, YyyyMmW> {
}
