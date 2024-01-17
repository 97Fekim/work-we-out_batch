package com.fekim.workweout.batch.job.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apikey;

    @Value("${coolsms.api.secret}")
    private String apiSecretKey;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apikey, apiSecretKey, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendSms(String senderPhone, String receiverPhone, String content) {
        Message message = new Message();

        message.setFrom(senderPhone);
        message.setTo(receiverPhone);
        message.setText(content);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        return response;

    }

}
