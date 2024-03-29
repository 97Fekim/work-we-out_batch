package com.fekim.workweout.batch.repository.date.entity;


import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmDd;
import com.fekim.workweout.batch.repository.date.entity.key.YyyyMmW;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Date")
@Table(name="TBL_DATE",
        indexes = { @Index(name = "IDX_TBL_DATE_FOR_SEARCH_WEEK", columnList = "CUOF_YYYY, CUOF_MM, CUOF_WEEK"),
                @Index(name = "IDX_TBL_DATE_FOR_SEARCH_MM", columnList = "CUOF_YYYY, CUOF_MM")
        }
)
public class Date {

    @EmbeddedId
    private YyyyMmDd yyyyMmDd;

    @Embedded
    private YyyyMmW yyyyMmW;

    @Column(name="DAY_OF_WEEK_CLSF_CD", nullable = false, columnDefinition = "VARCHAR(3)")
    private String dayOfWeekClsfCd;

    @Column(name="HOLY_DAY_YN", nullable = false, columnDefinition = "VARCHAR(1)")
    private String holyDayYn;

}
