package com.oplinnovate.notification.service;



import com.oplinnovate.notification.model.Notification;
import com.oplinnovate.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(BankNotificationService.class);
    private static final int MAX_RETRIES = 3;

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    public BankNotificationService(NotificationRepository notificationRepository, EmailService emailService,SmsService smsService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.smsService=smsService;
    }

    @Transactional
    public void notifyUser(Notification notification) {
        int retryCount = 0;
        boolean success = false;
        while (retryCount < MAX_RETRIES && !success) {
            try {
                if (notification.isUrgent()) {
                    smsService.sendSms(notification.getMobile(), notification.getMessage());
                } else {
                    emailService.sendEmail(notification.getRecipient(), "Bank Notification", notification.getMessage());
                }
                notification.setStatus("SENT");
                success = true;
                logger.info("Notification processed successfully for recipient: {}", notification.getRecipient());
            } catch (Exception e) {
                retryCount++;
                logger.error("Failed to notify user: {}", retryCount, e.getMessage(), e);
                notification.setStatus("FAILED");
                throw new RuntimeException("Failed to notify user", e);

            }
        }
        notificationRepository.save(notification);
        if (!success) {
            // Handle sending to DLQ or alerting
            logger.error("Notification failed after {} attempts: {}", MAX_RETRIES, notification);
        }
    }
}