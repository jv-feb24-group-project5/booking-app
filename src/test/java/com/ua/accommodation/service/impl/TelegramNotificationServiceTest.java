package com.ua.accommodation.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {

    @Mock
    Update update;

    @Mock
    Message message;

    @InjectMocks
    TelegramNotificationService telegramNotificationService;

    private final String botUsername = "testUsername";
    private final String botToken = "testToken";
    private final String groupId = "testGroupId";

    @BeforeEach
    void setUp() {
        telegramNotificationService = new TelegramNotificationService(botUsername, botToken, groupId);
    }

    @Test
    void getBotUsername_NoParameters_ReturnsBotUsername() {
        assertEquals(botUsername, telegramNotificationService.getBotUsername());
    }

    @Test
    void getBotToken_NoParameters_ReturnsBotToken() {
        assertEquals(botToken, telegramNotificationService.getBotToken());
    }

    @Test
    void onUpdateReceived_NewChatId_AddsChatId() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getChatId()).thenReturn(12345L);

        telegramNotificationService.onUpdateReceived(update);

        List<Long> chatIds = telegramNotificationService.getChatIds();
        assertTrue(chatIds.contains(12345L));
    }

    @Test
    void onUpdateReceived_ExistingChatId_DoesNotAddChatId() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getChatId()).thenReturn(12345L);

        telegramNotificationService.onUpdateReceived(update);
        telegramNotificationService.onUpdateReceived(update);

        List<Long> chatIds = telegramNotificationService.getChatIds();
        assertEquals(1, chatIds.size());
    }
}