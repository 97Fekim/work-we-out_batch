package com.fekim.workweout.batch.repository.stat.entity;

import com.fekim.workweout.batch.repository.stat.entity.key.YyyyMmMbr;
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
@Entity(name = "MonthlyWkoutStatRslt")
@Table(name="MONTHLY_WKOUT_STAT_RSLT")
public class MonthlyWkoutStatRslt {

    @EmbeddedId
    private YyyyMmMbr yyyyMmMbr;

    @Column(name="STAT_RSLT_TITLE", nullable = false, columnDefinition = "VARCHAR2(255)")
    private String statRsltTitle;

    @Column(name="STAT_RSLT_CONTENT", nullable = false, columnDefinition = "VARCHAR2(4000)")
    private String statRsltContent;

    @Column(name="SMS_SEND_RSLT_CLSF_CD", nullable = false, columnDefinition = "VARCHAR2(2)")
    private String smsSendRsltClsfCd;

}
