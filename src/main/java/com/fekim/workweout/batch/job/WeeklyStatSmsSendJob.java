package com.fekim.workweout.batch.job;

import com.fekim.workweout.batch.repository.member.entity.Member;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Log4j2
@RequiredArgsConstructor
@Configuration
public class WeeklyStatSmsSendJob {

    private final EntityManagerFactory entityManagerFactory;

    int chunkSize = 1;

    @Bean
    public Job job_WeeklyStatSmsSendJob(JobRepository jobRepository, Step step01) {
        return new JobBuilder("WeeklyStatSmsSendJob", jobRepository)
                .start(step01)
                //.listener()   // TODO : beforeStep / afterStep
                .build();
    }

    @Bean
    public Step step01_WeeklyStatSmsSendJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step01", jobRepository)
                .<Member,Member>chunk(chunkSize, transactionManager)
                .reader(reader_step01_WeeklyStatSmsSendJob())
                //.processor(processor())
                .writer(writer_step01_WeeklyStatSmsSendJob())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Member> reader_step01_WeeklyStatSmsSendJob() {
        return new JpaPagingItemReaderBuilder<Member>()
                .name("reader_step01_WeeklyStatSmsSendJob")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select M from Member M ")    // TODO : JPA Query
                .build();
    }

    @Bean
    public ItemWriter<Member> writer_step01_WeeklyStatSmsSendJob() {
        return list -> {
            for(Member member : list){
                System.out.println("====================member info====================");
                System.out.println(member.toString());
                System.out.println("====================member info====================");
            }
        };
    }




    // TODO : beforeStep/AfterStep Listener

}
