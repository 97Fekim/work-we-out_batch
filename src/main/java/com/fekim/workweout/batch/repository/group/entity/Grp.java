package com.fekim.workweout.batch.repository.group.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Grp")
@Table(name="GRP")
@SequenceGenerator(
        name = "SEQ_GRP_GENERATOR"
        , sequenceName = "SEQ_GRP"
        , initialValue = 1
        , allocationSize = 1
)
public class Grp {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GRP_GENERATOR")
    @Column(name="GRP_ID", nullable = false, columnDefinition = "NUMBER(15)")
    private Long grpId;

    @Column(name="GRP_NM", nullable = false, columnDefinition = "VARCHAR2(20)")
    private String grpNm;

    @Column(name="SRT_DT", nullable = true, columnDefinition = "VARCHAR2(8)")
    private String srtDt;

}
