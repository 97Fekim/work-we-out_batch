package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.stat.entity.MonthlyWkoutStatRslt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MonthlyWkoutStatRsltRepository extends JpaRepository<MonthlyWkoutStatRslt, YyyyMm> {

    @Query(value = "" +
            "select count(MR) " +
            "from MonthlyWkoutStatRslt MR " +
            "where MR.yyyyMmMbr.yyyyMm.cuofYyyy = :#{#yyyyMm.cuofYyyy} " +
            "  and MR.yyyyMmMbr.yyyyMm.cuofMm = :#{#yyyyMm.cuofMm} " +
            "  and MR.smsSendRsltClsfCd = :#{#smsSendRsltClsfCd} ")
    Long findMonthlyTotalCntBySmsSendRsltClsfCd(@Param("yyyyMm") YyyyMm yyyyMm,
                                                @Param("smsSendRsltClsfCd") String smsSendRsltClsfCd);

}
