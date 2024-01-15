package com.fekim.workweout.batch.job;

import com.fekim.workweout.batch.repository.member.entity.Member;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Configuration
public class WeeklyStatSmsSendJob {

    private final EntityManagerFactory entityManagerFactory;

    int chunkSize = 1;

    @Qualifier("WeeklyStatSmsSendJob")
    @Bean
    public Job job_WeeklyStatSmsSendJob(JobRepository jobRepository, Step step01) {
        return new JobBuilder("WeeklyStatSmsSendJob", jobRepository)
                .start(step01)
                //.listener()   // TODO : beforeStep / afterStep
                .build();
    }

    @JobScope
    @Bean
    public Step step01_WeeklyStatSmsSendJob(JobRepository jobRepository,
                                            PlatformTransactionManager transactionManager,
                                            @Value("#{jobParameters[yyyyMmW]}") String yyyyMmW) {
        return new StepBuilder("step01", jobRepository)
                .<Member,Member>chunk(chunkSize, transactionManager)
                .reader(reader_step01_WeeklyStatSmsSendJob(yyyyMmW))
                //.processor(processor())
                .writer(writer_step01_WeeklyStatSmsSendJob(yyyyMmW))
                .build();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<Member> reader_step01_WeeklyStatSmsSendJob(@Value("#{jobParameters[yyyyMmW]}") String yyyyMmW) {

        String queryString
                = "select M" +
                "  from Member M " +
                "  where M.mbrId = :mbrId";
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("mbrId", 1L);

        return new JpaPagingItemReaderBuilder<Member>()
                .name("reader_step01_WeeklyStatSmsSendJob")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString(queryString)    // TODO : JPA Query
                .parameterValues(parameters)
                .build();
    }

    @StepScope
    @Bean
    public ItemWriter<Member> writer_step01_WeeklyStatSmsSendJob(@Value("#{jobParameters[yyyyMmW]}") String yyyyMmW) {
        return list -> {
            for(Member member : list){
                System.out.println("====================member info====================");
                System.out.println(member.getMbrId()+"/"+member.getMbrNm());
                System.out.println("날짜 = " + yyyyMmW);
                System.out.println("====================member info====================");
            }
        };
    }


    // TODO : beforeStep/AfterStep Listener

}
