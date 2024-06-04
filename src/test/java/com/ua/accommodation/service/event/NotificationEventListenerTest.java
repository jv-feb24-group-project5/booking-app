package com.ua.accommodation.service.event;

import static org.mockito.Mockito.verify;

import com.ua.accommodation.service.impl.TelegramNotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationEventListenerTest {
    private static final String MESSAGE = "Test message";
    @Mock
    private TelegramNotificationService telegramNotificationService;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @DisplayName("Handle event should call sendNotification method form "
            + "telegramNotificationService")
    @Test
    void handleEvent_shouldCallSendNotification() {
        String message = MESSAGE;
        NotificationEvent event = new NotificationEvent(this, message);

        notificationEventListener.handleEvent(event);

        verify(telegramNotificationService).sendNotification(message);
    }
}
