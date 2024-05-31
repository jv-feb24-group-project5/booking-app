package com.ua.accommodation.service.impl;

import com.ua.accommodation.service.NotificationService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramNotificationService extends TelegramLongPollingBot
        implements NotificationService {

    private final List<Long> chatIds = new ArrayList<>();
    private final String botUsername;
    private final String botToken;
    private final String groupId;

    public TelegramNotificationService(@Value("${telegram.bot.username}") String botUsername,
                                       @Value("${telegram.bot.token}") String botToken,
                                       @Value("${telegram.bot.group.id}") String groupId) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.groupId = groupId;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            if (!chatIds.contains(chatId)) {
                chatIds.add(chatId);
            }
        }
    }

    @Override
    public void sendNotification(String notification) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupId);
        sendMessage.setText(notification);
        send(sendMessage);
    }

    private void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message to chat " + message.getChatId(), e);
        }
    }
}
