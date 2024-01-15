package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.stat.entity.WeeklyWkoutStatRslt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeeklyWkoutStatRsltRepository extends JpaRepository<WeeklyWkoutStatRslt, YyyyMmW> {

    @Query(value = "" +
            "select count(WR) " +
            "from WeeklyWkoutStatRslt WR " +
            "where WR.yyyyMmWMbr.yyyyMmW.cuofYyyy = :#{#yyyyMmW.cuofYyyy} " +
            "  and WR.yyyyMmWMbr.yyyyMmW.cuofMm = :#{#yyyyMmW.cuofMm} " +
            "  and WR.yyyyMmWMbr.yyyyMmW.cuofWeek = :#{#yyyyMmW.cuofWeek}" +
            "  and WR.smsSendRsltClsfCd = :#{#smsSendRsltClsfCd} ")
    Long findWeeklyTotalCntBySmsSendRsltClsfCd(@Param("yyyyMmW") YyyyMmW yyyyMmW,
                                               @Param("smsSendRsltClsfCd") String smsSendRsltClsfCd);

}
