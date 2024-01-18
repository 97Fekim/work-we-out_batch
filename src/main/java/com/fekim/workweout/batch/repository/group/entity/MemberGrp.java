package com.fekim.workweout.batch.repository.group.entity;

import com.fekim.workweout.batch.repository.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "MemberGrp")
@Table(name="MEMBER_GRP",
        indexes = {
                @Index(name = "IDX_MEMBER_GRP_MBR_ID", columnList = "MBR_ID"),
                @Index(name = "IDX_MEMBER_GRP_GRP_ID", columnList = "GRP_ID")
        }
)
@SequenceGenerator(
        name = "SEQ_MEMBER_GRP_GENERATOR"
        , sequenceName = "SEQ_MEMBER_GRP"
        , initialValue = 1
        , allocationSize = 1
)
public class MemberGrp {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_GRP_GENERATOR")
    @Column(name="MBR_GRP_ID", nullable = false, columnDefinition = "NUMBER(27)")
    private Long mbrGrpId;

    @ManyToOne
    @JoinColumn(name = "MBR_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "GRP_ID")
    private Grp grp;

    @Column(name="JOINED_DT", nullable = true, columnDefinition = "VARCHAR2(8)")
    private String joinedDt;

}
