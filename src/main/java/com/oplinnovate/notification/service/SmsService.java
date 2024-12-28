package com.oplinnovate.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
    public void sendSms(String toPhoneNumber, String messageBody) {
        Twilio.init(accountSid, authToken);

        try{
        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber), // To number
                new PhoneNumber(fromNumber), // From number
                messageBody // SMS body
        ).create();
        logger.info("Message sent successfully with SID: {}", message.getSid());
        }catch(Exception e){
            logger.error("Failed to send SMS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS", e);
        }

    }
}