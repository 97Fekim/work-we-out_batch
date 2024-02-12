package com.fekim.workweout.batch.scheduler;

import com.fekim.workweout.batch.repository.date.DateRepository;
import com.fekim.workweout.batch.repository.date.entity.Date;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMm;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmDd;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Log4j2
@EnableScheduling
@Component
@RequiredArgsConstructor
public class StatSmsSendJobScheduler {

    @Autowired
    @Qualifier("WeeklyStatSmsSendJob")
    private Job weeklyStatSmsSendJob;

    @Autowired
    @Qualifier("MonthlyStatSmsSendJob")
    private Job monthlyStatSmsSendJob;

    private final JobLauncher jobLauncher;

    private final DateRepository dateRepository;

    //@Scheduled(fixedDelay = 10000L) // for test
    @Scheduled(cron = "0 30 8 ? * MON") // 매주 월요일 아침 08:30
    public void runWeeklyStatSmsSendJob() throws Exception {
        // (1) 전날 조회
        YyyyMmDd yyyyMmDd = getYesterDay();

        // (2) 전날을 key로 하여, 기준 YyyyMmW 조회
        Date cuofDate = dateRepository.findById(yyyyMmDd).get();
        YyyyMmW yyyyMmW = YyyyMmW
                .builder()
                .cuofYyyy(cuofDate.getYyyyMmW().getCuofYyyy())
                .cuofMm(cuofDate.getYyyyMmW().getCuofMm())
                .cuofWeek(cuofDate.getYyyyMmW().getCuofWeek())
                .build();

        // (3) 배치 호출
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("yyyyMmW", ""+yyyyMmW.getCuofYyyy()+yyyyMmW.getCuofMm()+yyyyMmW.getCuofWeek())
                .addString("reSendYn", "N")
                .toJobParameters();

        jobLauncher.run(weeklyStatSmsSendJob, jobParameters);

    }

    //@Scheduled(fixedDelay = 10000L) // for test
    @Scheduled(cron = "0 30 8 * * 1") // 매일 1일 아침 08:30 실행
    public void runMonthlyStatSmsSendJob() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate firstMondayOfMonth = now.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        if (now.equals(firstMondayOfMonth)) { // 오늘(월요일)이 이번달의 첫 월요일인 경우 배치실행
            // 전날 조회
            YyyyMmDd yyyyMmDd = getYesterDay();

            // (2) 전날 YYYY/MM 으로 기준 YyyyMm 생성
            YyyyMm yyyyMm = YyyyMm
                    .builder()
                    .cuofYyyy(yyyyMmDd.getYyyy())
                    .cuofMm(yyyyMmDd.getMm())
                    .build();

            // (3) 배치 실행
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("yyyyMm", ""+yyyyMm.getCuofYyyy()+yyyyMm.getCuofMm())
                    .addString("reSendYn", "N")
                    .addString("dummy", "10001")
                    .toJobParameters();

            jobLauncher.run(monthlyStatSmsSendJob, jobParameters);

        }
    }

    private YyyyMmDd getYesterDay() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -1);
        String yyyyMmDd = simpleDateFormat.format(calendar.getTime());

        String yyyy = yyyyMmDd.substring(0, 4);
        String mm = yyyyMmDd.substring(4,6);
        String dd = yyyyMmDd.substring(6,8);

        return YyyyMmDd.builder()
                .yyyy(yyyy)
                .mm(mm)
                .dd(dd)
                .build();
    }

}
