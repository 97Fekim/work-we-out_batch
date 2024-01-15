package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.stat.entity.MonthlyWkoutStatRslt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyWkoutStatRsltRepository extends JpaRepository<MonthlyWkoutStatRslt, YyyyMmW> {
}
