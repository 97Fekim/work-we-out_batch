package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.stat.StatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class StatRepositoryTest {

    @Autowired
    private StatRepository statRepository;

    @Test
    void findWeeklyMethodTotalSetsTest() {
        List<Object[]> weeklyMethodTotalSets = statRepository.findWeeklyMethodTotalSets(
                1L, YyyyMmW
                        .builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .cuofWeek("2")
                        .build()
        );

        System.out.println("================list start================");
        for (Object[] objects : weeklyMethodTotalSets) {
            System.out.println((String)objects[0] + " : " + (Long)objects[1]);
            System.out.println();
        }
        System.out.println("================list end================");

    }

    @Test
    void findMonthlyMethodWeiIncs() {
        statRepository.findMonthlyMethodWeiIncs(
                1L,
                YyyyMm.builder().cuofYyyy("2024").cuofMm("01").build(),
                YyyyMm.builder().cuofYyyy("2024").cuofMm("02").build()
        );
    }

    @Test
    void findWeelMethodWeiIncs() {
        List<Object[]> objects = statRepository.findWeeklyMethodWeiIncs(
                1L,
                YyyyMmW.builder().cuofYyyy("2024").cuofMm("01").cuofWeek("2").build(),
                YyyyMmW.builder().cuofYyyy("2024").cuofMm("01").cuofWeek("1").build()
        );

        System.out.println("================list start================");
        for (Object[] object : objects) {
            System.out.println(String.valueOf(object[1]));
            System.out.println(((BigDecimal)object[2]).longValue());
            System.out.println(((BigDecimal)object[3]).longValue());
            System.out.println(((BigDecimal)object[4]).longValue());
            System.out.println();
        }
        System.out.println("================list end================");

    }

    @Test
    void findAllMethodInWeekTest() {
        List<Long> methods = statRepository.findAllMethodInWeek(
                1L,
                YyyyMmW.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .cuofWeek("2")
                        .build()
        );
        System.out.println(methods.toString());
    }

    @Test
    void findMethodMaxWeiInWeekTest() {
        System.out.println(
                statRepository.findMethodMaxWeiInWeek(
                        1L,
                        1L,
                        YyyyMmW.builder().cuofYyyy("2024").cuofMm("01").cuofWeek("2").build()
                )
        );

    }

    @Test
    void findAllMethodInMonthTest() {
        List<Long> methods = statRepository.findAllMethodInMonth(
                1L,
                YyyyMm.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .build()
        );
        System.out.println(methods.toString());
    }

    @Test
    void findMethodMaxWeiInMonthTest() {
        System.out.println(
                statRepository.findMethodMaxWeiInMonth(
                        1L,
                        1L,
                        YyyyMm.builder().cuofYyyy("2024").cuofMm("01").build()
                )
        );

    }

    @Test
    void findWeekGrpMemberTotalWkoutDaysCntTest() {
        List<Object[]> objects = statRepository.findWeekGrpMemberTotalWkoutDaysCnt(
                1L,
                YyyyMmW.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .cuofWeek("1")
                        .build()
        );

        System.out.println("================list start================");
        for (Object[] object : objects) {
            System.out.println(object[0]+"/"+object[1]+"/"+object[2]+"/"+object[3]+"/"+object[4]);
            System.out.println();
        }
        System.out.println("================list end================");
    }

    @Test
    void findMonthGrpMemberTotalWkoutDaysCntTest() {
        List<Object[]> objects = statRepository.findMonthGrpMemberTotalWkoutDaysCnt(
                1L,
                YyyyMm.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .build()
        );

        System.out.println("================list start================");
        for (Object[] object : objects) {
            System.out.println(object[0]+"/"+object[1]+"/"+object[2]+"/"+object[3]);
            System.out.println();
        }
        System.out.println("================list end================");
    }

    @Test
    void findWeekGrpTargetPartTotalSetsTest() {
        List<Object[]> objects = statRepository.findWeekGrpTargetPartTotalSets(
                1L,
                YyyyMmW.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .cuofWeek("1")
                        .build()
        );
        System.out.println("================list start================");
        for (Object[] object : objects) {
            System.out.println(object[0]+"/"+object[1]);
            System.out.println();
        }
        System.out.println("================list end================");
    }

}
