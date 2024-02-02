package com.fekim.workweout.batch.ondemand;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/stat/")
@RequiredArgsConstructor
public class StatApiController {

    @GetMapping("/manage/weekly-re-send-failed-sms")
    ResponseEntity<String> reSendWeeklyFailedSms(@RequestParam("yyyyMmW") String yyyyMmW) {
        System.out.println(yyyyMmW);

        return new ResponseEntity<>("01", HttpStatus.OK);
    }

    @GetMapping("/manage/monthly-re-send-failed-sms")
    ResponseEntity<String> reSendMonthlyFailedSms(HttpSession session,
                                                  @RequestParam("yyyyMm") String yyyyMm) {
        System.out.println(session.getAttribute("LOGIN_MEMBER") != null ? "로그인유저 있음" : "로그인유저 없음");

        return new ResponseEntity<>("01", HttpStatus.OK);
    }


}
