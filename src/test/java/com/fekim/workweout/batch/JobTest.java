package com.fekim.workweout.batch;

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

    @Test
    public void WeeklyStatSmsDendJobTest() throws Exception{

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("yyyyMmW", "2024011")
                .addString("reSendYn", "Y")
                .addString("dummy", "6")
                .toJobParameters();

        jobLauncher.run(job, jobParameters);

    }


}
