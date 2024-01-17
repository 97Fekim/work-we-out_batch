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
            "where M.mbrId = :#{#mbrId} " +
            "  and M.statSmsSendYn = 'Y' ")
    Member findSmsReceiverById(@Param("mbrId") Long mbrId);


}
