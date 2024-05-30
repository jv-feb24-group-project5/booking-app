package com.ua.accommodation.telegram;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final List<Long> chatIds = new ArrayList<>();

    private final String botUsername;
    private final String botToken;

    public MyTelegramBot(@Value("${telegram.bot.username}") String botUsername,
                         @Value("${telegram.bot.token}") String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
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
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();

            if (!chatIds.contains(chatId)) {
                chatIds.add(chatId);
            }

            handleIncomingMessage(chatId, text);
        }
    }

    private void handleIncomingMessage(Long chatId, String text) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("You sent: " + text);

        send(response);
    }

    private void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message to chat " + message.getChatId(), e);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void sendScheduledMessage() {
        for (Long chatId : chatIds) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("Scheduled message");
            send(message);
        }
    }
}
