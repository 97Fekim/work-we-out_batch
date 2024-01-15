package com.fekim.workweout.batch.repository.jnal;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmDd;
import com.fekim.workweout.batch.repository.jnal.entity.WkoutJnal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WkoutJnalRepository extends JpaRepository<WkoutJnal, Long> {

    @Query (value =
            "SELECT " +
            "    TD.YYYY     AS yyyy, " +
            "    TD.MM       AS mm, " +
            "    TD.DD       AS dd, " +
            "    JNALS.PARTS AS parts " +
            "FROM TBL_DATE TD " +
            "LEFT OUTER JOIN " +
            "    (SELECT WJ.YYYY AS yyyy, " +
            "            WJ.MM   AS mm, " +
            "            WJ.DD   AS dd, " +
            "            LISTAGG(DISTINCT WM.TARGET_PART, ',') WITHIN GROUP(ORDER BY WJ.YYYY, WJ.MM, WJ.DD) AS PARTS " +
            "     FROM WKOUT_JNAL WJ " +
            "     JOIN MEMBER M " +
            "     ON WJ.MBR_ID = M.MBR_ID " +
            "     JOIN WKOUT_JNAL_METHOD WJM " +
            "     ON WJM.JNAL_ID = WJ.JNAL_ID " +
            "     JOIN WKOUT_METHOD WM " +
            "     ON WM.METHOD_ID = WJM.METHOD_ID " +
            "     WHERE M.MBR_ID = :#{#mbrId} " +
            "     GROUP BY WJ.YYYY, WJ.MM, WJ.DD) JNALS " +
            "ON  TD.YYYY = JNALS.YYYY " +
            "AND TD.MM = JNALS.MM " +
            "AND TD.DD = JNALS.DD " +
            "WHERE TD.YYYY = :#{#yyyyMm.cuofYyyy} " +
            "  AND TD.MM = :#{#yyyyMm.cuofMm} " +
            "ORDER BY yyyy, mm, dd; "
            , nativeQuery = true)
    List<Object[]> findOneMonthCalendarObjects(@Param("mbrId")Long mbrId, @Param("yyyyMm") YyyyMm yyyyMm);

    @Query (value = "select J " +
            "from WkoutJnal J " +
            "where J.member.mbrId = :#{#mbrId} " +
            "  and J.yyyyMmDd.yyyy = :#{#yyyyMmDd.yyyy} " +
            "  and J.yyyyMmDd.mm = :#{#yyyyMmDd.mm} " +
            "  and J.yyyyMmDd.dd = :#{#yyyyMmDd.dd}")
    List<WkoutJnal> findWkoutJnalsByMbrIdAndYyyyMmDd(@Param("mbrId")Long mbrId, @Param("yyyyMmDd") YyyyMmDd yyyyMmDd);

    @Query(value = "" +
            "SELECT " +
            "    TD.YYYY     AS yyyy, " +
            "    TD.MM       AS mm, " +
            "    TD.DD       AS dd, " +
            "    MEMBERS.memberGrps AS memberGrps " +
            "FROM TBL_DATE TD " +
            "LEFT OUTER JOIN " +
            "    (SELECT WJ.YYYY AS yyyy, " +
            "            WJ.MM   AS mm, " +
            "            WJ.DD   AS dd, " +
            "            LISTAGG(DISTINCT " +
            "            MG.MBR_GRP_ID || '/' || " +
            "            M.MBR_ID || '/' || " +
            "            M.MBR_NM || '/' || " +
            "            NVL(M.PROF_IMG_PATH, 'EMPTY') , ',') WITHIN GROUP(ORDER BY M.MBR_ID) AS memberGrps " +
            "     FROM WKOUT_JNAL WJ " +
            "     JOIN MEMBER M " +
            "     ON WJ.MBR_ID = M.MBR_ID " +
            "     JOIN MEMBER_GRP MG " +
            "     ON M.MBR_ID = MG.MBR_ID " +
            "     JOIN GRP G " +
            "     ON G.GRP_ID = MG.GRP_ID " +
            "     WHERE G.GRP_ID = :#{#grpId} " +
            "     GROUP BY WJ.YYYY, WJ.MM, WJ.DD) MEMBERS " +
            "ON  TD.YYYY = MEMBERS.YYYY " +
            "AND TD.MM = MEMBERS.MM " +
            "AND TD.DD = MEMBERS.DD " +
            "WHERE TD.YYYY = :#{#yyyyMm.cuofYyyy} " +
            "  AND TD.MM = :#{#yyyyMm.cuofMm} " +
            "ORDER BY yyyy, mm, dd; " +
            "", nativeQuery = true)
    List<Object[]> findOneMonthGrpCalendarObject(@Param("grpId")Long grpId, @Param("yyyyMm")YyyyMm yyyyMm);

    @Query (value = "select J " +
            "from WkoutJnal J " +
            "join MemberGrp MG " +
            "on J.member.mbrId = MG.member.mbrId " +
            "where MG.grp.grpId = :#{#grpId} " +
            "  and J.yyyyMmDd.yyyy = :#{#yyyyMmDd.yyyy} " +
            "  and J.yyyyMmDd.mm = :#{#yyyyMmDd.mm} " +
            "  and J.yyyyMmDd.dd = :#{#yyyyMmDd.dd}")
    List<WkoutJnal> findWkoutJnalsByGrpIdAndYyyyMmDd(@Param("grpId")Long grpId, @Param("yyyyMmDd")YyyyMmDd yyyyMmDd);

}
