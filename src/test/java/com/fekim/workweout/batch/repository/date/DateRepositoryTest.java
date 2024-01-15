package com.fekim.workweout.batch.repository.date;

import com.fekim.workweout.batch.repository.date.DateRepository;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DateRepositoryTest {

    @Autowired
    private DateRepository dateRepository;

    @Test
    void findBeforeCuofYyyyMmWTest() {

        List<Object[]> beforeCuofYyyyMmW = dateRepository.findBeforeCuofYyyyMmW(
                YyyyMmW.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .cuofWeek("1")
                        .build(),
                1L
        );

        System.out.println(String.valueOf(beforeCuofYyyyMmW.get(0)[0]));
        System.out.println(String.valueOf(beforeCuofYyyyMmW.get(0)[1]));
        System.out.println(String.valueOf(beforeCuofYyyyMmW.get(0)[2]));
    }

    @Test
    void findBeforeCuofYyyyMmTest() {

        List<Object[]> beforeCuofYyyyMmW = dateRepository.findBeforeCuofYyyyMm(
                YyyyMm.builder()
                        .cuofYyyy("2024")
                        .cuofMm("01")
                        .build(),
                1L
        );

        System.out.println(String.valueOf(beforeCuofYyyyMmW.get(0)[0]));
        System.out.println(String.valueOf(beforeCuofYyyyMmW.get(0)[1]));
    }

}
