package com.fekim.workweout.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

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
                .addString("yyyyMmW", "2023082")
                .addString("reSendYn", "N")
                .toJobParameters();

        jobLauncher.run(job, jobParameters);

    }


}
