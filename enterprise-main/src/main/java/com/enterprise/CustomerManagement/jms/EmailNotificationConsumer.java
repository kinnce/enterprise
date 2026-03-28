package com.enterprise.CustomerManagement.jms;

import com.enterprise.CustomerManagement.model.dto.WelcomeEmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationConsumer {

    @JmsListener(destination = "${app.queue.email}")
    public void receiveWelcomeEmail(WelcomeEmailMessage message) {
        log.info("Получено сообщение для отправки email: {}", message);
        try {
            Thread.sleep(2000);
            log.info("[ИМИТАЦИЯ] Приветственное письмо отправлено на адрес: {}", message.getEmail());
            log.info("Email успешно обработан для клиента ID: {}", message.getCustomerId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to send email", e);
        }
    }
}