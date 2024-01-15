package com.fekim.workweout.batch.repository.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Member")
@Table(name="MEMBER")
@SequenceGenerator(
    name = "SEQ_MEMBER_GENERATOR"
    , sequenceName = "SEQ_MEMBER"
    , initialValue = 1
    , allocationSize = 1
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_GENERATOR")
    @Column(name="MBR_ID", nullable = false, columnDefinition = "NUMBER(12)")
    private Long mbrId;

    @Column(name="MBR_NM", nullable = false, columnDefinition = "VARCHAR2(20)")
    private String mbrNm;

    @Column(name="PHONE", nullable = false, columnDefinition = "VARCHAR2(20)")
    private String phone;

    @Column(name="EMAIL", nullable = false, columnDefinition = "VARCHAR2(320)", unique = true)
    private String email;

    @Column(name="STAT_SMS_SEND_YN", nullable = false, columnDefinition = "VARCHAR2(1)")
    private String statSmsSendYn;

    @Column(name="MBR_ROLE_CLSF_CD", nullable = false, columnDefinition = "VARCHAR2(2)")
    private String mbrRoleClsfCd;

    @Column(name="PROF_IMG_PATH", nullable = true, columnDefinition = "VARCHAR2(300)")
    private String profImgPath;

    @Column(name="MBR_STAT_CLSF_CD", nullable = false, columnDefinition = "VARCHAR2(2)")
    private String mbrStatClsfCd;

}
