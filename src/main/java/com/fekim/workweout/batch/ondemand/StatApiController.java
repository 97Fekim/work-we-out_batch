package com.fekim.workweout.batch.ondemand;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

/**
 * 개인 통계 문자발송 작업 온디맨드 배치 API 컨트롤러
 * */
@Log4j2
@RestController
@RequestMapping("/stat/")
@RequiredArgsConstructor
public class StatApiController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("WeeklyStatSmsSendJob")
    private Job weeklyStatSmsSendJob;

    @Autowired
    @Qualifier("MonthlyStatSmsSendJob")
    private Job monthlyStatSmsSendJob;

    /**
     * 01. 주간통계 문자발송 작업 재처리
     *  - IN = YYYY/MM/W
     *  - OUT = 처리결과코드(01:정상 / 02:오류)
     * */
    @GetMapping("/manage/weekly-re-send-failed-sms")
    ResponseEntity<String> reSendWeeklyFailedSms(@RequestParam("yyyyMmW") String yyyyMmW) {

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("yyyyMmW", yyyyMmW)
                    .addString("reSendYn", "Y")  // 재실행여부 Y
                    .addDate("requestDate", new Date())
                    .toJobParameters();
            jobLauncher.run(weeklyStatSmsSendJob, jobParameters);
        } catch (Exception e) {
            // 처리 실패시 처리결과코드(02:오류)
            return new ResponseEntity<>("02", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 처리 완료시 처리결과코드(01:정상)
        return new ResponseEntity<>("01", HttpStatus.OK);
    }

    /**
     * 02. 월간통계 문자발송 작업 재처리
     *  - IN = YYYY/MM
     *  - OUT = 처리결과코드(01:정상 / 02:오류)
     * */
    @GetMapping("/manage/monthly-re-send-failed-sms")
    ResponseEntity<String> reSendMonthlyFailedSms(@RequestParam("yyyyMm") String yyyyMm) {

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("yyyyMm", yyyyMm)
                    .addString("reSendYn", "Y")  // 재실행여부 Y
                    .addDate("requestDate", new Date())
                    .toJobParameters();
            jobLauncher.run(monthlyStatSmsSendJob, jobParameters);
        } catch (Exception e) {
            // 처리 실패시 처리결과코드(02:오류)
            return new ResponseEntity<>("02", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 처리 완료시 처리결과코드(01:정상)
        return new ResponseEntity<>("01", HttpStatus.OK);
    }


}
