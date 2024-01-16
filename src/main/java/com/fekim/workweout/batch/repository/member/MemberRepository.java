package com.fekim.workweout.batch.repository.member;

import com.fekim.workweout.batch.repository.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "" +
            "select M " +
            "from Member M " +
            "where M.mbrId = :#{#mbrId}" +
            "  and M.mbrStatClsfCd = :#{#mbrStatClsfCd}")
    Member findMemberByIdAndMbrStatClsfCd(@Param("mbrId") Long mbrId,
                                          @Param("mbrStatClsfCd") String mbrStatClsfCd);

    @Query(value = "" +
            "select M " +
            "from Member M " +
            "join WkoutJnal J on M.mbrId = J.member.mbrId " +
            "join Date D " +
            "  on D.yyyyMmDd.yyyy = J.yyyyMmDd.yyyy " +
            " and D.yyyyMmDd.mm = J.yyyyMmDd.mm " +
            "where D.yyyyMmDd.dd = J.yyyyMmDd.dd " +
            "  and D.yyyyMmW.cuofYyyy = :cuofYyyy " +
            "  and D.yyyyMmW.cuofMm = :cuofMm " +
            "  and D.yyyyMmW.cuofWeek = :cuofWeek " +
            "group by M.mbrId " +
            "order by M.mbrId ")
    List<Member> findMemberList (@Param("cuofYyyy") String cuofYyyy,
                                 @Param("cuofMm") String cuofMm,
                                 @Param("cuofWeek") String cuofWeek);

}
