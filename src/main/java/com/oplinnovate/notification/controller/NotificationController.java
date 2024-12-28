package com.oplinnovate.notification.controller;

import com.oplinnovate.notification.model.Notification;
import com.oplinnovate.notification.service.BankNotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final BankNotificationService bankNotificationService;

    public NotificationController(BankNotificationService bankNotificationService) {
        this.bankNotificationService = bankNotificationService;
    }

    @PostMapping
    public ResponseEntity<String> createNotification(@Valid @RequestBody Notification notification) {
        try {
            bankNotificationService.notifyUser(notification);
            return new ResponseEntity<>("Notification sent successfully!", HttpStatus.OK);
    }catch (Exception e) {
            logger.error("Failed to create notification: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to send notification", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}