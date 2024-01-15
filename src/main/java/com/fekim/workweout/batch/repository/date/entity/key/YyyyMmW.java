package com.fekim.workweout.batch.repository.date.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YyyyMmW implements Serializable {

    @Column(name="CUOF_YYYY", nullable = false, columnDefinition = "VARCHAR2(4)")
    private String cuofYyyy;

    @Column(name="CUOF_MM", nullable = false, columnDefinition = "VARCHAR2(2)")
    private String cuofMm;

    @Column(name="CUOF_WEEK", nullable = false, columnDefinition = "VARCHAR2(1)")
    private String cuofWeek;

}
