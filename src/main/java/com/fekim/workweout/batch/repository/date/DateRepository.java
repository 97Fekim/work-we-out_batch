package com.fekim.workweout.batch.repository.date;

import com.fekim.workweout.batch.repository.date.entity.Date;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmDd;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DateRepository extends JpaRepository<Date, YyyyMmDd> {

    @Query(value = "" +
            "SELECT  " +
            "  BF_CUOF_YYYY AS bfCuofYyyy, " +
            "  BF_CUOF_MM AS bfCuofMm, " +
            "  BF_CUOF_WEEK AS bfCuofWeek " +
            "FROM  " +
            "  (SELECT  " +
            "     CUOF_YYYY,  " +
            "     CUOF_MM,  " +
            "     CUOF_WEEK, " +
            "     LAG(CUOF_YYYY, :#{#range} ) OVER (ORDER BY CUOF_YYYY, CUOF_MM, CUOF_WEEK) AS BF_CUOF_YYYY, " +
            "     LAG(CUOF_MM, :#{#range} ) OVER (ORDER BY CUOF_YYYY, CUOF_MM, CUOF_WEEK) AS BF_CUOF_MM, " +
            "     LAG(CUOF_WEEK, :#{#range} ) OVER (ORDER BY CUOF_YYYY, CUOF_MM, CUOF_WEEK) AS BF_CUOF_WEEK " +
            "   FROM TBL_DATE " +
            "   GROUP BY CUOF_YYYY, CUOF_MM, CUOF_WEEK " +
            "   ORDER BY CUOF_YYYY, CUOF_MM, CUOF_WEEK ) CUOF_DATE " +
            "WHERE 1=1  " +
            "  AND CUOF_YYYY = :#{#cuofYyyyMmW.cuofYyyy} " +
            "  AND CUOF_MM = :#{#cuofYyyyMmW.cuofMm} " +
            "  AND CUOF_WEEK = :#{#cuofYyyyMmW.cuofWeek} " +
            "ORDER BY BF_CUOF_YYYY, BF_CUOF_MM, BF_CUOF_WEEK; "
            , nativeQuery = true)
    List<Object[]> findBeforeCuofYyyyMmW(@Param("cuofYyyyMmW") YyyyMmW cuofYyyyMmW,
                                   @Param("range") Long range);

    @Query(value = "" +
            "SELECT  " +
            "  BF_CUOF_YYYY AS bfCuofYyyy, " +
            "  BF_CUOF_MM AS bfCuofMm " +
            "FROM  " +
            "  (SELECT  " +
            "     CUOF_YYYY,  " +
            "     CUOF_MM,  " +
            "     LAG(CUOF_YYYY, :#{#range} ) OVER (ORDER BY CUOF_YYYY, CUOF_MM) AS BF_CUOF_YYYY, " +
            "     LAG(CUOF_MM, :#{#range} ) OVER (ORDER BY CUOF_YYYY, CUOF_MM) AS BF_CUOF_MM " +
            "   FROM TBL_DATE " +
            "   GROUP BY CUOF_YYYY, CUOF_MM " +
            "   ORDER BY CUOF_YYYY, CUOF_MM ) CUOF_DATE " +
            "WHERE 1=1  " +
            "  AND CUOF_YYYY = :#{#cuofYyyyMm.cuofYyyy} " +
            "  AND CUOF_MM = :#{#cuofYyyyMm.cuofMm} " +
            "ORDER BY BF_CUOF_YYYY, BF_CUOF_MM; "
            , nativeQuery = true)
    List<Object[]> findBeforeCuofYyyyMm(@Param("cuofYyyyMm") YyyyMm cuofYyyyMm,
                                         @Param("range") Long range);

}
