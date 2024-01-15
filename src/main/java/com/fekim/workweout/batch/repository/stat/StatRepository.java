package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.jnal.entity.WkoutJnalMethod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatRepository extends Repository<WkoutJnalMethod, Long> {

    @Query(value = "" +
            "select WM.targetPart AS targetPart," +
            "       SUM(WJM.sets) AS totalSets " +
            "from WkoutJnal WJ " +
            "join Date D on WJ.yyyyMmDd = D.yyyyMmDd " +
            "join WkoutJnalMethod WJM on WJ.jnalId = WJM.wkoutJnal.jnalId " +
            "join WkoutMethod WM on WJM.wkoutMethod.methodId = WM.methodId " +
            "where WJ.member.mbrId = :#{#mbrId} " +
            "  and D.yyyyMmW.cuofYyyy = :#{#yyyyMmW.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMmW.cuofMm} " +
            "  and D.yyyyMmW.cuofWeek = :#{#yyyyMmW.cuofWeek} " +
            "group by WM.methodNm " +
            "order by WM.methodId ")
    List<Object[]> findWeeklyMethodTotalSets(@Param("mbrId") Long mbrId, @Param("yyyyMmW") YyyyMmW yyyyMmW);

    @Query(value = "" +
            "select WM.targetPart AS targetPart," +
            "       SUM(WJM.sets) AS totalSets " +
            "from WkoutJnal WJ " +
            "join Date D on WJ.yyyyMmDd = D.yyyyMmDd " +
            "join WkoutJnalMethod WJM on WJ.jnalId = WJM.wkoutJnal.jnalId " +
            "join WkoutMethod WM on WJM.wkoutMethod.methodId = WM.methodId " +
            "where WJ.member.mbrId = :#{#mbrId} " +
            "  and D.yyyyMmW.cuofYyyy = :#{#yyyyMm.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMm.cuofMm} " +
            "group by WM.methodNm " +
            "order by WM.methodId ")
    List<Object[]> findMonthlyMethodTotalSets(@Param("mbrId") Long mbrId, @Param("yyyyMm") YyyyMm yyyyMm);


    @Query(value = "" +
            "SELECT " +
            "  BEFORE_WEEK.METHOD_ID AS methodId,  " +
            "  BEFORE_WEEK.METHOD_NM AS methodNm,  " +
            "  BEFORE_WEEK.MAX_WEIGHT AS bfWeekMaxWeight,  " +
            "  CUR_WEEK.MAX_WEIGHT AS curWeekMaxWeight,  " +
            "  CUR_WEEK.MAX_WEIGHT - BEFORE_WEEK.MAX_WEIGHT AS weightIncrease  " +
            "FROM " +
            "   (SELECT WM.METHOD_ID, WM.METHOD_NM, MAX(WJM.WEIGHT) AS MAX_WEIGHT  " +
            "    FROM " +
            "      WKOUT_JNAL WJ, " +
            "      WKOUT_JNAL_METHOD WJM, " +
            "      WKOUT_METHOD WM, " +
            "      TBL_DATE D " +
            "    WHERE 1=1  " +
            "      AND WJ.JNAL_ID = WJM.JNAL_ID " +
            "      AND WJM.METHOD_ID = WM.METHOD_ID " +
            "      AND WJ.YYYY = D.YYYY " +
            "      AND WJ.MM = D.MM " +
            "      AND WJ.DD = D.DD " +
            "      AND WJ.MBR_ID = :#{#mbrId} " +
            "      AND D.CUOF_YYYY = :#{#bfYyyyMmW.cuofYyyy} " +
            "      AND D.CUOF_MM = :#{#bfYyyyMmW.cuofMm} " +
            "      AND D.CUOF_WEEK = :#{#bfYyyyMmW.cuofWeek} " +
            "    GROUP BY WM.METHOD_ID) BEFORE_WEEK, " +
            "   (SELECT WM.METHOD_ID, WM.METHOD_NM, MAX(WJM.WEIGHT)  AS MAX_WEIGHT  " +
            "    FROM " +
            "      WKOUT_JNAL WJ, " +
            "      WKOUT_JNAL_METHOD WJM, " +
            "      WKOUT_METHOD WM, " +
            "      TBL_DATE D " +
            "    WHERE 1=1  " +
            "      AND WJ.JNAL_ID = WJM.JNAL_ID " +
            "      AND WJM.METHOD_ID = WM.METHOD_ID " +
            "      AND WJ.YYYY = D.YYYY " +
            "      AND WJ.MM = D.MM " +
            "      AND WJ.DD = D.DD " +
            "      AND WJ.MBR_ID = :#{#mbrId} " +
            "      AND D.CUOF_YYYY = :#{#curYyyyMmW.cuofYyyy} " +
            "      AND D.CUOF_MM = :#{#curYyyyMmW.cuofMm} " +
            "      AND D.CUOF_WEEK = :#{#curYyyyMmW.cuofWeek} " +
            "GROUP BY WM.METHOD_ID) CUR_WEEK " +
            "WHERE BEFORE_WEEK.METHOD_ID = CUR_WEEK.METHOD_ID " +
            "ORDER BY methodId;"
            , nativeQuery = true)
    List<Object[]> findWeeklyMethodWeiIncs(@Param("mbrId") Long mbrId,
                                           @Param("bfYyyyMmW") YyyyMmW bfYyyyMmW,
                                           @Param("curYyyyMmW") YyyyMmW curYyyyMmW);

    @Query(value = "" +
            "SELECT " +
            "  BEFORE_MONTH.METHOD_ID AS methodId,  " +
            "  BEFORE_MONTH.METHOD_NM AS methodNm,  " +
            "  BEFORE_MONTH.MAX_WEIGHT AS bfMonthMaxWeight,  " +
            "  CUR_MONTH.MAX_WEIGHT AS curMonthMaxWeight,  " +
            "  CUR_MONTH.MAX_WEIGHT - BEFORE_MONTH.MAX_WEIGHT AS weightIncrease  " +
            "FROM " +
            "   (SELECT WM.METHOD_ID, WM.METHOD_NM, MAX(WJM.WEIGHT) AS MAX_WEIGHT  " +
            "   FROM " +
            "   WKOUT_JNAL WJ, " +
            "   WKOUT_JNAL_METHOD WJM, " +
            "   WKOUT_METHOD WM, " +
            "   TBL_DATE D " +
            "   WHERE 1=1  " +
            "     AND WJ.JNAL_ID = WJM.JNAL_ID " +
            "     AND WJM.METHOD_ID = WM.METHOD_ID " +
            "     AND WJ.YYYY = D.YYYY " +
            "     AND WJ.MM = D.MM " +
            "     AND WJ.DD = D.DD " +
            "     AND WJ.MBR_ID = :#{#mbrId} " +
            "     AND D.CUOF_YYYY = :#{#bfYyyyMm.cuofYyyy} " +
            "     AND D.CUOF_MM = :#{#bfYyyyMm.cuofMm} " +
            "   GROUP BY WM.METHOD_ID) BEFORE_MONTH, " +
            "   (SELECT WM.METHOD_ID, WM.METHOD_NM, MAX(WJM.WEIGHT)  AS MAX_WEIGHT  " +
            "    FROM " +
            "      WKOUT_JNAL WJ, " +
            "      WKOUT_JNAL_METHOD WJM, " +
            "      WKOUT_METHOD WM, " +
            "      TBL_DATE D " +
            "    WHERE 1=1  " +
            "      AND WJ.JNAL_ID = WJM.JNAL_ID " +
            "      AND WJM.METHOD_ID = WM.METHOD_ID " +
            "      AND WJ.YYYY = D.YYYY " +
            "      AND WJ.MM = D.MM " +
            "      AND WJ.DD = D.DD " +
            "      AND WJ.MBR_ID = :#{#mbrId} " +
            "      AND D.CUOF_YYYY = :#{#curYyyyMm.cuofYyyy} " +
            "      AND D.CUOF_MM = :#{#curYyyyMm.cuofMm} " +
            "GROUP BY WM.METHOD_ID) CUR_MONTH " +
            "WHERE BEFORE_MONTH.METHOD_ID = CUR_MONTH.METHOD_ID " +
            "ORDER BY methodId;"
            , nativeQuery = true)
    List<Object[]> findMonthlyMethodWeiIncs(@Param("mbrId") Long mbrId,
                                           @Param("bfYyyyMm") YyyyMm bfYyyyMm,
                                           @Param("curYyyyMm") YyyyMm curYyyyMm);

    @Query(value = "" +
            "select WM.methodId AS methodId " +
            "from WkoutJnalMethod WJM " +
            "join WJM.wkoutJnal WJ " +
            "join WJM.wkoutMethod WM " +
            "join Date D " +
            "  on D.yyyyMmDd.yyyy = WJ.yyyyMmDd.yyyy " +
            " and D.yyyyMmDd.mm = WJ.yyyyMmDd.mm " +
            " and D.yyyyMmDd.dd = WJ.yyyyMmDd.dd " +
            "where WJ.member.mbrId = :#{#mbrId} " +
            "  and D.yyyyMmW.cuofYyyy = :#{#yyyyMmW.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMmW.cuofMm} " +
            "  and D.yyyyMmW.cuofWeek = :#{#yyyyMmW.cuofWeek} " +
            "group by WM.methodId " +
            "order by WM.methodId ")
    List<Long> findAllMethodInWeek(@Param("mbrId") Long mbrId,
                                   @Param("yyyyMmW") YyyyMmW yyyyMmW);


    @Query(value = "" +
            "SELECT  " +
            "  MAX(WJM.WEIGHT) AS MAX_WEI " +
            "FROM " +
            "  WKOUT_JNAL WJ, " +
            "  WKOUT_JNAL_METHOD WJM, " +
            "  WKOUT_METHOD WM, " +
            "  TBL_DATE TD " +
            "WHERE 1=1 " +
            "  AND WJ.JNAL_ID = WJM.JNAL_ID " +
            "  AND WJ.YYYY = TD.YYYY " +
            "  AND WJ.MM = TD.MM " +
            "  AND WJ.DD = TD.DD " +
            "  AND WJ.MBR_ID = :#{#mbrId} " +
            "  AND TD.CUOF_YYYY = :#{#yyyyMmW.cuofYyyy} " +
            "  AND TD.CUOF_MM = :#{#yyyyMmW.cuofMm} " +
            "  AND TD.CUOF_WEEK = :#{#yyyyMmW.cuofWeek} " +
            "  AND WJM.METHOD_ID = WM.METHOD_ID " +
            "  AND WJM.METHOD_ID = :#{#methodId} ; " +
            "", nativeQuery = true)
    Long findMethodMaxWeiInWeek(@Param("mbrId") Long mbrId,
                                      @Param("methodId") Long methodId,
                                      @Param("yyyyMmW") YyyyMmW yyyyMmW);

    @Query(value = "" +
            "select WM.methodId AS methodId " +
            "from WkoutJnalMethod WJM " +
            "join WJM.wkoutJnal WJ " +
            "join WJM.wkoutMethod WM " +
            "join Date D " +
            "  on D.yyyyMmDd.yyyy = WJ.yyyyMmDd.yyyy " +
            " and D.yyyyMmDd.mm = WJ.yyyyMmDd.mm " +
            " and D.yyyyMmDd.dd = WJ.yyyyMmDd.dd " +
            "where WJ.member.mbrId = :#{#mbrId} " +
            "  and D.yyyyMmW.cuofYyyy = :#{#yyyyMm.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMm.cuofMm} " +
            "group by WM.methodId " +
            "order by WM.methodId ")
    List<Long> findAllMethodInMonth(@Param("mbrId") Long mbrId,
                                   @Param("yyyyMm") YyyyMm yyyyMm);

    @Query(value = "" +
            "SELECT  " +
            "  MAX(WJM.WEIGHT) AS MAX_WEI " +
            "FROM " +
            "  WKOUT_JNAL WJ, " +
            "  WKOUT_JNAL_METHOD WJM, " +
            "  WKOUT_METHOD WM, " +
            "  TBL_DATE TD " +
            "WHERE 1=1 " +
            "  AND WJ.JNAL_ID = WJM.JNAL_ID " +
            "  AND WJ.YYYY = TD.YYYY " +
            "  AND WJ.MM = TD.MM " +
            "  AND WJ.DD = TD.DD " +
            "  AND WJ.MBR_ID = :#{#mbrId} " +
            "  AND TD.CUOF_YYYY = :#{#yyyyMm.cuofYyyy} " +
            "  AND TD.CUOF_MM = :#{#yyyyMm.cuofMm} " +
            "  AND WJM.METHOD_ID = WM.METHOD_ID " +
            "  AND WJM.METHOD_ID = :#{#methodId} ; " +
            "", nativeQuery = true)
    Long findMethodMaxWeiInMonth(@Param("mbrId") Long mbrId,
                                @Param("methodId") Long methodId,
                                @Param("yyyyMm") YyyyMm yyyyMm);

    @Query(value = "" +
            "select " +
            "  WJ.member.mbrId AS mbrId," +
            "  D.yyyyMmW.cuofYyyy AS cuofYyyy," +
            "  D.yyyyMmW.cuofMm AS cuofMm, " +
            "  D.yyyyMmW.cuofWeek AS cuofWeek, " +
            "  count(distinct D.yyyyMmDd) AS wkoutDaysCnt " +
            "from WkoutJnal WJ " +
            "join MemberGrp MG on WJ.member.mbrId = MG.member.mbrId " +
            "join Date D " +
            "  on D.yyyyMmDd.yyyy = WJ.yyyyMmDd.yyyy " +
            " and D.yyyyMmDd.mm = WJ.yyyyMmDd.mm " +
            " and D.yyyyMmDd.dd = WJ.yyyyMmDd.dd " +
            "where D.yyyyMmW.cuofYyyy = :#{#yyyyMmW.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMmW.cuofMm} " +
            "  and D.yyyyMmW.cuofWeek = :#{#yyyyMmW.cuofWeek} " +
            "  and MG.grp.grpId = :#{#grpId} " +
            "group by WJ.member.mbrId, D.yyyyMmW.cuofYyyy, D.yyyyMmW.cuofMm, D.yyyyMmW.cuofWeek " +
            "order by WJ.member.mbrId, D.yyyyMmW.cuofYyyy, D.yyyyMmW.cuofMm, D.yyyyMmW.cuofWeek ")
    List<Object[]> findWeekGrpMemberTotalWkoutDaysCnt(@Param("grpId") Long grpId,
                                                      @Param("yyyyMmW") YyyyMmW yyyyMmW);

    @Query(value = "" +
            "select " +
            "  WJ.member.mbrId AS mbrId," +
            "  D.yyyyMmW.cuofYyyy AS cuofYyyy," +
            "  D.yyyyMmW.cuofMm AS cuofMm, " +
            "  count(distinct D.yyyyMmDd) AS wkoutDaysCnt " +
            "from WkoutJnal WJ " +
            "join MemberGrp MG on WJ.member.mbrId = MG.member.mbrId " +
            "join Date D " +
            "  on D.yyyyMmDd.yyyy = WJ.yyyyMmDd.yyyy " +
            " and D.yyyyMmDd.mm = WJ.yyyyMmDd.mm " +
            " and D.yyyyMmDd.dd = WJ.yyyyMmDd.dd " +
            "where D.yyyyMmW.cuofYyyy = :#{#yyyyMm.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMm.cuofMm} " +
            "  and MG.grp.grpId = :#{#grpId} " +
            "group by WJ.member.mbrId, D.yyyyMmW.cuofYyyy, D.yyyyMmW.cuofMm " +
            "order by WJ.member.mbrId, D.yyyyMmW.cuofYyyy, D.yyyyMmW.cuofMm ")
    List<Object[]> findMonthGrpMemberTotalWkoutDaysCnt(@Param("grpId") Long grpId,
                                                      @Param("yyyyMm") YyyyMm yyyyMm);

    @Query(value = "" +
            "select " +
            "  WM.targetPart as targetPart, " +
            "  sum(WJM.sets) as totalSets " +
            "from MemberGrp MG " +
            "join WkoutJnal WJ on WJ.member.mbrId = MG.member.mbrId " +
            "join WkoutJnalMethod WJM on WJM.wkoutJnal.jnalId = WJ.jnalId " +
            "join WkoutMethod WM on WM.methodId = WJM.wkoutMethod.methodId " +
            "join Date D " +
            "  on D.yyyyMmDd.yyyy = WJ.yyyyMmDd.yyyy " +
            " and D.yyyyMmDd.mm = WJ.yyyyMmDd.mm " +
            " and D.yyyyMmDd.dd = WJ.yyyyMmDd.dd " +
            "where MG.grp.grpId = :#{#grpId} " +
            "  and D.yyyyMmW.cuofYyyy = :#{#yyyyMmW.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMmW.cuofMm} " +
            "  and D.yyyyMmW.cuofWeek = :#{#yyyyMmW.cuofWeek} " +
            "group by WM.targetPart " +
            "order by WM.targetPart ")
    List<Object[]> findWeekGrpTargetPartTotalSets(@Param("grpId") Long grpId,
                                                  @Param("yyyyMmW") YyyyMmW yyyyMmW);

    @Query(value = "" +
            "select " +
            "  WM.targetPart as targetPart, " +
            "  sum(WJM.sets) as totalSets " +
            "from MemberGrp MG " +
            "join WkoutJnal WJ on WJ.member.mbrId = MG.member.mbrId " +
            "join WkoutJnalMethod WJM on WJM.wkoutJnal.jnalId = WJ.jnalId " +
            "join WkoutMethod WM on WM.methodId = WJM.wkoutMethod.methodId " +
            "join Date D " +
            "  on D.yyyyMmDd.yyyy = WJ.yyyyMmDd.yyyy " +
            " and D.yyyyMmDd.mm = WJ.yyyyMmDd.mm " +
            " and D.yyyyMmDd.dd = WJ.yyyyMmDd.dd " +
            "where MG.grp.grpId = :#{#grpId} " +
            "  and D.yyyyMmW.cuofYyyy = :#{#yyyyMm.cuofYyyy} " +
            "  and D.yyyyMmW.cuofMm = :#{#yyyyMm.cuofMm} " +
            "group by WM.targetPart " +
            "order by WM.targetPart ")
    List<Object[]> findMonthGrpTargetPartTotalSets(@Param("grpId") Long grpId,
                                                  @Param("yyyyMm") YyyyMm yyyyMm);


}
