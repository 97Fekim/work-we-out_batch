package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.stat.entity.MonthlyWkoutStatSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyWkoutStatScheduleRepository extends JpaRepository<MonthlyWkoutStatSchedule, YyyyMmW> {
}
