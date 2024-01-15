package com.fekim.workweout.batch.repository.stat.entity;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "MonthlyWkoutStatSchedule")
@Table(name="MONTHLY_WKOUT_STAT_SCHEDULE")
public class MonthlyWkoutStatSchedule {

    @EmbeddedId
    private YyyyMm yyyyMm;

    @Column(name="STAT_CPLN_YN", nullable = false, columnDefinition = "VARCHAR2(1)")
    private String statCplnYn;

}
