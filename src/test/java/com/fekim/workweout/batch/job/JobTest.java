package com.fekim.workweout.batch.job;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("WeeklyStatSmsSendJob")
    private Job job;

    @Autowired
    @Qualifier("MonthlyStatSmsSendJob")
    private Job job2;

    @Test
    public void WeeklyStatSmsSendJobTest() throws Exception{

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("yyyyMmW", "2023092")
                .addString("reSendYn", "N")
                .toJobParameters();

        jobLauncher.run(job, jobParameters);

    }

    @Test
    public void MonthlyStatSmsSendJobTest() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("yyyyMm", "202401")
                .addString("reSendYn", "Y")
                .addString("dummy", "1")
                .toJobParameters();

        jobLauncher.run(job2, jobParameters);
    }


}
