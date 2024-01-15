package com.fekim.workweout.batch.repository.stat;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.stat.WeeklyWkoutStatRsltRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StatRsltRepositoryTest {

    @Autowired
    private WeeklyWkoutStatRsltRepository weeklyWkoutStatRsltRepository;

    @Test
    void findWeeklyTotalCntBySmsSendRsltClsfCdTest() {
        Long cnt = weeklyWkoutStatRsltRepository.findWeeklyTotalCntBySmsSendRsltClsfCd(
                YyyyMmW.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .cuofWeek("1")
                        .build()
                ,"01"
        );
        Assertions.assertThat(true);
        System.out.println("==================cnt=================");
        System.out.println(cnt);
        System.out.println("==================cnt=================");
    }


}
