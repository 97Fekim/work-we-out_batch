package com.fekim.workweout.batch.repository.jnal;

import com.fekim.workweout.batch.repository.jnal.entity.WkoutJnalMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WkoutJnalMethodRepository extends JpaRepository<WkoutJnalMethod, Long> {

    @Query(value ="" +
            "select " +
            "WJM.jnalMethodId AS jnalMethodId, " +
            "WM.methodId AS methodId, " +
            "WM.methodNm AS methodNm, " +
            "WM.targetPart AS targetPart, " +
            "WJM.weight AS weight, " +
            "WJM.sets AS sets, " +
            "WJM.reps AS reps, " +
            "WJM.restTime AS restTime " +
            "from WkoutJnalMethod WJM " +
            "join WJM.wkoutMethod WM " +
            "where WJM.wkoutJnal.jnalId = :#{#jnalId}")
    List<Object[]> findAllByJnalId(@Param("jnalId") Long jnalId);

    @Modifying
    @Query(value = "" +
            "delete " +
            "from WkoutJnalMethod WJM " +
            "where WJM.wkoutJnal.jnalId = :#{#jnalId} ")
    void deleteByJnalId(@Param("jnalId") Long jnalId);

}
