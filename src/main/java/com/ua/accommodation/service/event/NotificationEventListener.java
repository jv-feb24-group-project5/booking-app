package com.ua.accommodation.service.event;

import com.ua.accommodation.service.impl.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final TelegramNotificationService telegramNotificationService;

    @EventListener
    public void handleEvent(NotificationEvent event) {
        telegramNotificationService.sendNotification(event.getMessage());
    }
}
