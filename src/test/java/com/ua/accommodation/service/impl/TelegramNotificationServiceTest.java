package com.ua.accommodation.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {

    public static final Long CHAT_ID = 12345L;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @InjectMocks
    private TelegramNotificationService telegramNotificationService;

    private final String botUsername = "testUsername";
    private final String botToken = "testToken";
    private final String groupId = "testGroupId";

    @BeforeEach
    void setUp() {
        telegramNotificationService =
                new TelegramNotificationService(botUsername, botToken, groupId);
    }

    @DisplayName("Get bot username should return valid username")
    @Test
    void getBotUsername_NoParameters_ReturnsBotUsername() {
        assertEquals(botUsername, telegramNotificationService.getBotUsername());
    }

    @DisplayName("Get bot token should return valid token")
    @Test
    void getBotToken_NoParameters_ReturnsBotToken() {
        assertEquals(botToken, telegramNotificationService.getBotToken());
    }

    @DisplayName("Chat id should be saved to chadIds list")
    @Test
    void onUpdateReceived_NewChatId_AddsChatId() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getChatId()).thenReturn(CHAT_ID);

        telegramNotificationService.onUpdateReceived(update);

        List<Long> chatIds = telegramNotificationService.getChatIds();
        assertTrue(chatIds.contains(CHAT_ID));
    }

    @DisplayName("Chat id shouldn't be saved to chadIds list"
            + " if it already present there")
    @Test
    void onUpdateReceived_ExistingChatId_DoesNotAddChatId() {
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getChatId()).thenReturn(CHAT_ID);
        int expected = 1;

        telegramNotificationService.onUpdateReceived(update);
        telegramNotificationService.onUpdateReceived(update);

        List<Long> chatIds = telegramNotificationService.getChatIds();
        assertEquals(expected, chatIds.size());
    }
}
