package com.fekim.workweout.batch.repository.jnal.entity;


import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmDd;
import com.fekim.workweout.batch.repository.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "WkoutJnal")
@Table(name="WKOUT_JNAL",
        indexes = @Index(name = "IDX_WKOUT_JNAL_ONE_DAY", columnList = "MBR_ID, YYYY, MM, DD")
)
@SequenceGenerator(
        name = "SEQ_WKOUT_JNAL_GENERATOR"
        , sequenceName = "SEQ_WKOUT_JNAL"
        , initialValue = 1
        , allocationSize = 1
)
public class WkoutJnal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WKOUT_JNAL_GENERATOR")
    @Column(name="JNAl_ID", nullable = false, columnDefinition = "NUMBER(15)")
    private Long jnalId;

    @ManyToOne
    @JoinColumn(name = "MBR_ID")
    private Member member;

    @Embedded
    private YyyyMmDd yyyyMmDd;

    @Column(name="COMMENTS", nullable = true, columnDefinition = "VARCHAR2(500)")
    private String comments;

}
