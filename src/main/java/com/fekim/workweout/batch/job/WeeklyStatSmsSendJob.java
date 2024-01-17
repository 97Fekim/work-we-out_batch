package com.fekim.workweout.batch.job;

import com.fekim.workweout.batch.job.util.SmsUtil;
import com.fekim.workweout.batch.repository.date.DateRepository;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import com.fekim.workweout.batch.repository.member.MemberRepository;
import com.fekim.workweout.batch.repository.member.entity.Member;
import com.fekim.workweout.batch.repository.stat.StatRepository;
import com.fekim.workweout.batch.repository.stat.WeeklyWkoutStatRsltRepository;
import com.fekim.workweout.batch.repository.stat.WeeklyWkoutStatScheduleRepository;
import com.fekim.workweout.batch.repository.stat.entity.WeeklyWkoutStatRslt;
import com.fekim.workweout.batch.repository.stat.entity.WeeklyWkoutStatSchedule;
import com.fekim.workweout.batch.repository.stat.entity.key.YyyyMmWMbr;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
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

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 01. 개인주간운동통계문자발송
 *  - 파라미터 : [ (1)Week정보 , (2)정기배치/온디맨드배치 구분코드 ]
 *  - In/Out 타입 : DB to DB
 *  - In/Out 상세 : 회원기본 to 주간운동통계내역
 *  - 타입 : Chunk-Oriented
 *  - 청크 크기 : 1000
 *  - 스케줄 : 매주 월요일 08:40
 *  - 처리대상 : 회원의 전 Week 월요일~일요일 운동일지
 * */
@Log4j2
@RequiredArgsConstructor
@Configuration
public class WeeklyStatSmsSendJob {

    private final EntityManagerFactory entityManagerFactory;
    private final WeeklyWkoutStatScheduleRepository weeklyWkoutStatScheduleRepository;
    private final WeeklyWkoutStatRsltRepository weeklyWkoutStatRsltRepository;
    private final StatRepository statRepository;
    private final DateRepository dateRepository;
    private final MemberRepository memberRepository;
    private final SmsUtil smsUtil;

    int chunkSize = 1000;

    @Bean
    @Qualifier("WeeklyStatSmsSendJob")
    public Job job_WeeklyStatSmsSendJob(JobRepository jobRepository,
                                        @Qualifier("step01_WeeklyStatSmsSendJob") Step step01,
                                        @Qualifier("weeklyExecutionListener") JobExecutionListener jobExecutionListener) {
        return new JobBuilder("WeeklyStatSmsSendJob", jobRepository)
                .start(step01)
                .listener(jobExecutionListener)
                .build();
    }

    @Bean
    @JobScope
    @Qualifier("step01_WeeklyStatSmsSendJob")
    public Step step01_WeeklyStatSmsSendJob(JobRepository jobRepository,
                                            PlatformTransactionManager transactionManager,
                                            @Value("#{jobParameters[yyyyMmW]}") String yyyyMmW,
                                            @Value("#{jobParameters[reSendYn]}") String reSendYn) {
        return new StepBuilder("step01", jobRepository)
                .allowStartIfComplete(true)
                .<Member, WeeklyWkoutStatRslt>chunk(chunkSize, transactionManager)
                .reader(reader_step01_WeeklyStatSmsSendJob(yyyyMmW, reSendYn))
                .processor(processor_step01_WeeklyStatSmsSendJob(yyyyMmW))
                .writer(writer_step01_WeeklyStatSmsSendJob(yyyyMmW))
                .build();
    }

    @StepScope
    @Bean
    public JpaPagingItemReader<Member> reader_step01_WeeklyStatSmsSendJob(@Value("#{jobParameters[yyyyMmW]}") String yyyyMmW,
                                                                          @Value("#{jobParameters[reSendYn]}") String reSendYn) {
        // (1) 기준Week Key로 변환
        YyyyMmW cuofYyyyMmW = makeYyyyMmW(yyyyMmW);

        // (2) 대상추출 SQL 생성
        String queryString = makeReadQuery(reSendYn);

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

            log.debug("[DEBUG]====================Processor Start====================");
            log.debug("[DEBUG] Member Info ↓↓↓↓ ");
            log.debug("[DEBUG] mbrId : " + member.getMbrId());
            log.debug("[DEBUG] mbrNm : " + member.getMbrNm());
            log.debug("[DEBUG]=======================================================");

            Long mbrId = member.getMbrId();
            String statRsltTitle = "";
            String statRsltContent = "";
            String smsSendRsltClsfCd = "";

            // (1) 통계결과 생성
            try {
                // (1-1) 문자 제목 생성
                statRsltTitle = makeStatRsltTitle(yyyyMmW);

                // (1-3) 문자 내용 생성
                statRsltContent = statRsltTitle
                                + "\n\n"
                                + makeStatRsltContent(member.getMbrId(), yyyyMmW);

                // (1-3) 처리결과[01:정상] 설정
                smsSendRsltClsfCd = "01";

            } catch (Exception e) {  // 처리중 오류가 발생할 경우 [02:발송실패] 처리한다.
                log.error("[ERROR]====================Error Occured In Stat Processing====================");
                log.error("[ERROR]====================Error Occured In Stat Processing====================");

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
                // (0) Logging
                log.debug("[DEBUG]====================ItemWriter Start====================");
                log.debug("[DEBUG] Week Info : " + yyyyMmW.toString());
                log.debug("[DEBUG] Stat Result Info ↓↓↓↓ ");
                log.debug("[DEBUG]  - Member Id : " + weeklyWkoutStatRslt.getYyyyMmWMbr().getMbrId());
                log.debug("[DEBUG]  - SMS Title : " + weeklyWkoutStatRslt.getStatRsltTitle());
                log.debug("[DEBUG]  - SMS Content : \n" + weeklyWkoutStatRslt.getStatRsltContent());
                log.debug("[DEBUG]  - SMS Send Status : " + weeklyWkoutStatRslt.getSmsSendRsltClsfCd());
                log.debug("[DEBUG]========================================================");

                try {
                    // (1) 문자발송한다.
                    Member receiver = memberRepository.findSmsReceiverById(weeklyWkoutStatRslt.getYyyyMmWMbr().getMbrId());
                    SingleMessageSentResponse smsResponse = smsUtil.sendSms(
                            "01090374099",
                            receiver.getPhone(),
                            weeklyWkoutStatRslt.getStatRsltContent()
                    );

                } catch (Exception e) {
                    log.error("[ERROR]====================Error Occured In Send message Processing====================");
                    log.error("[ERROR]====================Error Occured In Send message Processing====================");

                    weeklyWkoutStatRslt.setSmsSendRsltClsfCd("02");
                }

                // (2) 통계/문자발송 결과 DB에 반영한다.
                weeklyWkoutStatRsltRepository.save(weeklyWkoutStatRslt);
            }
        };
    }

    /* 전/후 처리 */
    @JobScope
    @Bean
    @Qualifier("weeklyExecutionListener")
    public JobExecutionListener weeklyExecutionListener() {

        return new JobExecutionListener() {

            @Override
            public void beforeJob(JobExecution jobExecution) throws InvalidParameterException {
                String yyyyMmW = jobExecution.getJobParameters().getString("yyyyMmW");
                String reSendYn = jobExecution.getJobParameters().getString("reSendYn");

                // (0) 입력값 체크
                if (yyyyMmW == null || yyyyMmW.isEmpty()
                        || reSendYn == null || reSendYn.isEmpty()) {
                    jobExecution.setExitStatus(ExitStatus.FAILED);
                    throw new InvalidParameterException();
                }

                log.debug("[DEBUG]====================BeforeJob Start====================");
                log.debug("[DEBUG] Parameter Info ↓↓↓↓ ");
                log.debug("[DEBUG]  - yyyyMmW : " + yyyyMmW);
                log.debug("[DEBUG]  - reSendYn : " + reSendYn);
                log.debug("[DEBUG]========================================================");


                // (1) 정기배치에 의해 실행된 경우
                //  - 재작업 방지를 위해 문자발송작업 완료여부를 체크한다.
                if (reSendYn.equals("N")) {

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
                if (reSendYn.equals("N")) {

                    WeeklyWkoutStatSchedule schedule = weeklyWkoutStatScheduleRepository.findById(makeYyyyMmW(yyyyMmW)).get();

                    schedule.setStatCplnYn("Y"); // dirty-checking
                }

                // (2) 온디맨드배치에 의해 실행된 경우
                //  - 작업완료여부와 관련된 처리를 하지 않는다.
                jobExecution.setExitStatus(ExitStatus.COMPLETED);
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
    private String makeReadQuery(String reSendYn) {
        String queryString = "";
        
        if (reSendYn.equals("N")) { // (1) 정기배치인 경우
             queryString = "select M " +
                     "from Member M " +
                     "join WkoutJnal J on M.mbrId = J.member.mbrId " +
                     "join Date D " +
                     "  on D.yyyyMmDd.yyyy = J.yyyyMmDd.yyyy " +
                     " and D.yyyyMmDd.mm = J.yyyyMmDd.mm " +
                     " and D.yyyyMmDd.dd = J.yyyyMmDd.dd " +
                     "where D.yyyyMmW.cuofYyyy = :cuofYyyy " +
                     "  and D.yyyyMmW.cuofMm = :cuofMm " +
                     "  and D.yyyyMmW.cuofWeek = :cuofWeek " +
                     "  and M.statSmsSendYn = 'Y' " +
                     "group by M.mbrId " +
                     "order by M.mbrId ";
        } else if (reSendYn.equals("Y")) { // (2) 재처리인 경우
            queryString = "select M " +
                    "from Member M " +
                    "join WeeklyWkoutStatRslt WR " +
                    "  on WR.yyyyMmWMbr.mbrId = M.mbrId " +
                    "where WR.yyyyMmWMbr.yyyyMmW.cuofYyyy = :cuofYyyy " +
                    "  and WR.yyyyMmWMbr.yyyyMmW.cuofMm = :cuofMm " +
                    "  and WR.yyyyMmWMbr.yyyyMmW.cuofWeek = :cuofWeek " +
                    "  and WR.smsSendRsltClsfCd = '02' " +
                    "  and M.statSmsSendYn = 'Y' " +
                    "group by M.mbrId " +
                    "order by M.mbrId ";
        } else {
            throw new InvalidParameterException();
        }

        return queryString;
    }
    
    /* 문자 제목 생성 */
    private String makeStatRsltTitle(String yyyyMmW) {
        YyyyMmW curYyyyMmW = makeYyyyMmW(yyyyMmW);

        return new StringBuilder()
                .append("[Work We Out] ")
                .append(curYyyyMmW.getCuofYyyy())
                .append("년 ")
                .append(curYyyyMmW.getCuofMm())
                .append("월 ")
                .append(curYyyyMmW.getCuofWeek())
                .append("주차 운동통계")
                .toString();
    }

    /* 문자 내용 생성 */
    private String makeStatRsltContent(Long mbrId, String yyyyMmW) {

        StringBuilder content = new StringBuilder();

        YyyyMmW cuofYyyyMmW = makeYyyyMmW(yyyyMmW);

        /* 1. 운동부위별 총 세트수 */
        List<Object[]> totalSets = statRepository.findWeeklyMethodTotalSets(mbrId, cuofYyyyMmW);
        content.append("[1. 운동부위별 총 세트수]\n");
        for (Object[] totalSet : totalSets) {
            content.append(" ▶ ").append(String.valueOf(totalSet[0]));
            content.append(" : ").append(String.valueOf(totalSet[1])).append("set\n");
        }
        content.append("\n");

        /* 2. 운동종목별 중량증감 */
        //  2-1. 이전 Week 정보 조회
        List<Object[]> bfYyyyMmWEntity = dateRepository.findBeforeCuofYyyyMmW(cuofYyyyMmW, 1L);
        YyyyMmW bfYyyyMmW = YyyyMmW.builder()
                .cuofYyyy(String.valueOf(bfYyyyMmWEntity.get(0)[0]))
                .cuofMm(String.valueOf(bfYyyyMmWEntity.get(0)[1]))
                .cuofWeek(String.valueOf(bfYyyyMmWEntity.get(0)[2]))
                .build();
        //  2-2. 중량증감 생성
        List<Object[]> weeklyMethodWeiIncs = statRepository.findWeeklyMethodWeiIncs(mbrId, bfYyyyMmW, cuofYyyyMmW);
        content.append("[2. 전 주 대비 운동종목별 중량 상승 추이]\n");
        for (Object[] weiInc : weeklyMethodWeiIncs) {

            String methodNm = String.valueOf(weiInc[1]);
            Long bfWei = ((BigDecimal)weiInc[2]).longValue();
            Long curWei = ((BigDecimal)weiInc[3]).longValue();
            Long incWei = ((BigDecimal)weiInc[4]).longValue();
            String incDec = incWei >= 0 ? "증가" : "감소";

            content.append(" ▶ ").append(methodNm).append("\n");
            content.append("  - 이전 주 최대무게 : ").append(bfWei).append("kg\n");
            content.append("  - 이번 주 최대무게 : ").append(curWei).append("kg\n");
            content.append("   → 1주일간 ").append(incWei).append("kg ").append(incDec).append("하였습니다.\n");
        }
        content.append("\n");

        return content.toString();
    }

}
