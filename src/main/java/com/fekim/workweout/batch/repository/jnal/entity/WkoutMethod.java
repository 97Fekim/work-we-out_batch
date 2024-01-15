package com.fekim.workweout.batch.repository.jnal.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "WkoutMethod")
@Table(name="WKOUT_METHOD")
@SequenceGenerator(
        name = "SEQ_WKOUT_METHOD_GENERATOR"
        , sequenceName = "SEQ_WKOUT_METHOD"
        , initialValue = 1
        , allocationSize = 1
)
public class WkoutMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WKOUT_METHOD_GENERATOR")
    @Column(name="METHOD_ID", nullable = false, columnDefinition = "NUMBER(6)")
    private Long methodId;

    @Column(name="METHOD_NM", nullable = false, columnDefinition = "VARCHAR2(100)")
    private String methodNm;

    @Column(name="TARGET_PART", nullable = false, columnDefinition = "VARCHAR2(10)")
    private String targetPart;

}
