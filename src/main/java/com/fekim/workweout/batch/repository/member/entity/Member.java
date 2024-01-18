package com.fekim.workweout.batch.repository.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name="PASSWORD", nullable = false, columnDefinition = "VARCHAR2(120)")
    private String password;

    @Column(name="STAT_SMS_SEND_YN", nullable = false, columnDefinition = "VARCHAR2(1)")
    private String statSmsSendYn;

    @Column(name="MBR_ROLE_CLSF_CD", nullable = false, columnDefinition = "VARCHAR2(40)")
    private String mbrRoleClsfCd;

    @Column(name="PROF_IMG_PATH", nullable = true, columnDefinition = "VARCHAR2(300)")
    private String profImgPath;

    @Column(name="MBR_STAT_CLSF_CD", nullable = false, columnDefinition = "VARCHAR2(40)")
    private String mbrStatClsfCd;
    
    /* 로그인 성공시, 인증정보를 반환 */
    public Authentication makeAuthentication() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ADMIN"));
        return new UsernamePasswordAuthenticationToken(this.getEmail(), null, authorityList);
    }

}