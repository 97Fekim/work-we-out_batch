package com.fekim.workweout.batch.repository.date.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class YyyyMm implements Serializable {

    @Column(name="CUOF_YYYY", nullable = false, columnDefinition = "VARCHAR2(4)")
    private String cuofYyyy;

    @Column(name="CUOF_MM", nullable = false, columnDefinition = "VARCHAR2(2)")
    private String cuofMm;
}
