package com.nikola.userhandlerbe2.bot;

import com.nikola.userhandlerbe2.services.CryptoCurrencyService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashSet;
import java.util.Set;

@Component
@EqualsAndHashCode(callSuper = true)
@Data
public class CryptoProphetBot extends TelegramLongPollingBot {
    public static CryptoProphetBot instance = getInstance();

    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    public static CryptoProphetBot getInstance() {
        if (instance == null) {
            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                CryptoProphetBot cryptoProphetBot = new CryptoProphetBot();
                telegramBotsApi.registerBot(cryptoProphetBot);
                instance = cryptoProphetBot;
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private CryptoProphetBot() {
        super();
    }

    private Set<UserDetails> users = new HashSet<>();
    @Override
    public String getBotUsername() {
        return "CryptoProphet";
    }

    @Override
    public String getBotToken() {
        return "6883568451:AAF-rEGSZCqytQkP6B9mrqV5f6GsUgZddrA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();
        var user = message.getFrom();
        System.out.println(message.getText());
        users.add(new UserDetails(user.getId(), null));
        System.out.println(user.getId());
        handleMessage(user.getId(), message.getText());
    }

    public void sendMessage(Long userId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId.toString())
                .text(message)
                .build();
        try {
            this.execute(sendMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleMessage(Long userId, String message) {
        String firstWord = message.split(" ")[0];
        String messageLowerCase = firstWord.toLowerCase();
        switch (messageLowerCase) {
            case "/start" -> {
                sendMessage(userId, "Welcome to CryptoProphet! Please use /help to see the list of available commands.");
            }
            case "/help" -> {
                sendMessage(userId, """
                        /start - Start the bot
                        /help - Show the list of available commands
                        /subscribe - Subscribe to a new cryptocurrency
                        /unsubscribe - Unsubscribe from a cryptocurrency
                        /charts - Show charts for a cryptocurrency
                        /news - Show news for a cryptocurrency
                        """);
            }
            case "/subscribe" -> {
            }
            case "/updatecrypto" -> {
                cryptoCurrencyService.updateCryptoCurrency(message.split(" ")[1]);
            }
            case "/unsubscribe" -> {
            }
            case "/charts" -> {
                //TODO call the charts endpoint of the secured API
            }
            case "/news" -> {
                //TODO call the news endpoint of the secured API
            }
            default -> {
                sendMessage(userId, "Unknown command. Please use /help to see the list of available commands.");
            }
        }
    }

}
