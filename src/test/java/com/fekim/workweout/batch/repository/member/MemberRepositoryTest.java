package com.fekim.workweout.batch.repository.member;

import com.fekim.workweout.batch.repository.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findMemberListTest() {
        List<Member> memberList = memberRepository.findMemberList("2024", "01", "1");
        for (Member member : memberList) {
            System.out.println(member.getMbrNm());
        }
    }

}
