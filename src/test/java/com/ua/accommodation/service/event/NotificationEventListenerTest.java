package com.ua.accommodation.service.event;

import static org.mockito.Mockito.verify;

import com.ua.accommodation.service.impl.TelegramNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationEventListenerTest {

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @Test
    void handleEvent_shouldCallSendNotification() {
        String message = "Test message";
        NotificationEvent event = new NotificationEvent(this, message);

        notificationEventListener.handleEvent(event);

        verify(telegramNotificationService).sendNotification(message);
    }
}
