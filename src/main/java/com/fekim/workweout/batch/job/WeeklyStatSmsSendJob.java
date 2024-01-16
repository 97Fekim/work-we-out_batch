package com.fekim.workweout.batch.job;

import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.member.entity.Member;
import com.fekim.workweout.batch.repository.stat.WeeklyWkoutStatRsltRepository;
import com.fekim.workweout.batch.repository.stat.WeeklyWkoutStatScheduleRepository;
import com.fekim.workweout.batch.repository.stat.entity.WeeklyWkoutStatRslt;
import com.fekim.workweout.batch.repository.stat.entity.WeeklyWkoutStatSchedule;
import com.fekim.workweout.batch.repository.stat.entity.key.YyyyMmWMbr;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
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

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;


/**
 * 01. 개인주간운동통계문자발송
 *  - 파라미터 : [ (1)Week정보 , (2)정기배치/온디맨드배치 구분코드 ]
 *  - In/Out 타입 : DB to DB
 *  - In/Out 상세 : 회원기본 to 주간운동통계내역
 *  - 타입 : Chunk-Oriented
 *  - 청크 크기 : 1
 *  - 스케줄 : 매주 일요일 23:40
 *  - 처리대상 : 회원의 월요일~일요일 운동일지
 * */
@Log4j2
@RequiredArgsConstructor
@Configuration
public class WeeklyStatSmsSendJob {

    private final EntityManagerFactory entityManagerFactory;
    private final WeeklyWkoutStatScheduleRepository weeklyWkoutStatScheduleRepository;
    private final WeeklyWkoutStatRsltRepository weeklyWkoutStatRsltRepository;

    int chunkSize = 1;

    @Qualifier("WeeklyStatSmsSendJob")
    @Bean
    public Job job_WeeklyStatSmsSendJob(JobRepository jobRepository, Step step01) {
        return new JobBuilder("WeeklyStatSmsSendJob", jobRepository)
                .start(step01)
                .listener(jobExecutionListener())
                .build();
    }

    @JobScope
    @Bean
    public Step step01_WeeklyStatSmsSendJob(JobRepository jobRepository,
                                            PlatformTransactionManager transactionManager,
                                            @Value("#{jobParameters[yyyyMmW]}") String yyyyMmW) {

        return new StepBuilder("step01", jobRepository)
                .<Member, WeeklyWkoutStatRslt>chunk(chunkSize, transactionManager)
                .reader(reader_step01_WeeklyStatSmsSendJob(yyyyMmW))
                .processor(processor_step01_WeeklyStatSmsSendJob(yyyyMmW))
                .writer(writer_step01_WeeklyStatSmsSendJob(yyyyMmW))
                .build();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<Member> reader_step01_WeeklyStatSmsSendJob(@Value("#{jobParameters[yyyyMmW]}") String yyyyMmW) {

        // (1) 기준Week Key로 변환
        YyyyMmW cuofYyyyMmW = makeYyyyMmW(yyyyMmW);

        // (2) 대상추출 SQL 생성
        String queryString = makeQueryWeeklySmsReceiver(cuofYyyyMmW);

        // (3) SQL where절 파라미터 생성
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cuofYyyy", cuofYyyyMmW.getCuofYyyy());
        parameters.put("cuofMm", cuofYyyyMmW.getCuofMm());
        parameters.put("cuofWeek", cuofYyyyMmW.getCuofWeek());

        // (4) 최종 ItemReader 생성
        return new JpaPagingItemReaderBuilder<Member>()
                .name("reader_step01_WeeklyStatSmsSendJob")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString(queryString)
                .parameterValues(parameters)
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Member, WeeklyWkoutStatRslt> processor_step01_WeeklyStatSmsSendJob(@Value("#{jobParameters[yyyyMmW]}") String yyyyMmW) {

        YyyyMmW cuofYyyyMmW = makeYyyyMmW(yyyyMmW);

        return member -> {

            Long mbrId = member.getMbrId();
            String statRsltTitle = "";
            String statRsltContent = "";
            String smsSendRsltClsfCd = "";

            // (1) 통계결과 생성
            try {
                statRsltTitle = "Dummy Title1";
                statRsltContent = "Dummy Content1";

            } catch (Exception e) {  // 처리중 오류가 발생할 경우 [02:발송실패] 처리한다.
                smsSendRsltClsfCd = "02";
            }

            // (2) 통계결과 조립
            return WeeklyWkoutStatRslt.builder()
                    .yyyyMmWMbr(YyyyMmWMbr.builder()
                            .yyyyMmW(YyyyMmW.builder()
                                    .cuofYyyy(cuofYyyyMmW.getCuofYyyy())
                                    .cuofMm(cuofYyyyMmW.getCuofMm())
                                    .cuofWeek(cuofYyyyMmW.getCuofWeek())
                                    .build())
                            .mbrId(mbrId)
                            .build())
                    .statRsltTitle(statRsltTitle)
                    .statRsltContent(statRsltContent)
                    .smsSendRsltClsfCd(smsSendRsltClsfCd)
                    .build();
        };

    }

    @StepScope
    @Bean
    public ItemWriter<WeeklyWkoutStatRslt> writer_step01_WeeklyStatSmsSendJob(@Value("#{jobParameters[yyyyMmW]}") String yyyyMmW) {
        return results -> {
            for(WeeklyWkoutStatRslt weeklyWkoutStatRslt : results){
                System.out.println("[DEBUG]====================Weekly Stat Sms Send reslut====================");
                System.out.println("[DEBUG] Week Info : " + yyyyMmW.toString());
                System.out.println("[DEBUG] Result Info ↓↓↓↓ ");
                System.out.println("[DEBUG]  - Member Id : " + weeklyWkoutStatRslt.getYyyyMmWMbr().getMbrId());
                System.out.println("[DEBUG]  - SMS Title : " + weeklyWkoutStatRslt.getStatRsltTitle());
                System.out.println("[DEBUG]  - SMS Content : " + weeklyWkoutStatRslt.getStatRsltContent());
                System.out.println("[DEBUG]  - SMS Send Status : " + weeklyWkoutStatRslt.getSmsSendRsltClsfCd());
                System.out.println("[DEBUG]====================Weekly Stat Sms Send reslut====================");

                // 통계결과 DB에 반영한다.
                weeklyWkoutStatRsltRepository.save(weeklyWkoutStatRslt);
            }
        };
    }


    /* 전/후 처리 */
    @JobScope
    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) throws InvalidParameterException {
                String yyyyMmW = jobExecution.getJobParameters().getString("yyyyMmW");
                String reSendYn = jobExecution.getJobParameters().getString("reSendYn");

                // (1) 정기배치에 의해 실행된 경우
                //  - 재작업 방지를 위해 문자발송작업 완료여부를 체크한다.
                if (reSendYn == null || reSendYn.equals("N")) {

                    WeeklyWkoutStatSchedule schedule = weeklyWkoutStatScheduleRepository.findById(makeYyyyMmW(yyyyMmW)).get();

                    // 이미 문자발송작업이 완료된 스케줄이라면 배치를 종료한다.
                    if (schedule.getStatCplnYn().equals("Y")) {
                        jobExecution.setExitStatus(ExitStatus.FAILED);
                        throw new InvalidParameterException();
                    }
                }

                // (2) 온디맨드배치에 의해 실행된 경우
                //  - 재작업이므로 문자발송작업 완료여부를 체크하지 않는다.
            }

            @Override
            @Transactional
            public void afterJob(JobExecution jobExecution) {
                String yyyyMmW = jobExecution.getJobParameters().getString("yyyyMmW");
                String reSendYn = jobExecution.getJobParameters().getString("reSendYn");

                // (1) 정기배치에 의해 실행된 경우
                //  - 작업이 완료되면 작업완료여부를 갱신한다.
                if (reSendYn == null || reSendYn.equals("N")) {

                    WeeklyWkoutStatSchedule schedule = weeklyWkoutStatScheduleRepository.findById(makeYyyyMmW(yyyyMmW)).get();

                    schedule.setStatCplnYn("Y"); // dirty-checking
                }

                // (2) 온디맨드배치에 의해 실행된 경우
                //  - 작업완료여부와 관련된 처리를 하지 않는다.
            }

        };
    }



    /* (String)yyyyMmW -> (YyyyMmW)yyyyMmW */
    private YyyyMmW makeYyyyMmW(String yyyyMmW) {
        String curYyyy = yyyyMmW.substring(0, 4);
        String curMm = yyyyMmW.substring(4,6);
        String curWeek = yyyyMmW.substring(6,7);

        return YyyyMmW
                .builder()
                .cuofYyyy(curYyyy)
                .cuofMm(curMm)
                .cuofWeek(curWeek)
                .build();
    }
    
    /* 대상회원 추출 SQL 생성 */
    private String makeQueryWeeklySmsReceiver(YyyyMmW yyyyMmW) {
        return "select M " +
                "from Member M " +
                "join WkoutJnal J on M.mbrId = J.member.mbrId " +
                "join Date D " +
                "  on D.yyyyMmDd.yyyy = J.yyyyMmDd.yyyy " +
                " and D.yyyyMmDd.mm = J.yyyyMmDd.mm " +
                "where D.yyyyMmDd.dd = J.yyyyMmDd.dd " +
                "  and D.yyyyMmW.cuofYyyy = :cuofYyyy " +
                "  and D.yyyyMmW.cuofMm = :cuofMm " +
                "  and D.yyyyMmW.cuofWeek = :cuofWeek " +
                "group by M.mbrId " +
                "order by M.mbrId ";
    }

}
