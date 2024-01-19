package com.fekim.workweout.batch.ondemand;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginTestController {

    @GetMapping("/member/connect")
    public ResponseEntity<String> connect(HttpSession session) {
        String result;

        Object email = session.getAttribute("LOGIN_MEMBER");
        if (email == null) {
            result = "로그인 되지 않음";
        } else {
            result = (String) email;
        }

        return ResponseEntity.ok(result);
    }
}
