package com.nikola.userhandlerbe2.bot;

import com.nikola.userhandlerbe2.services.CryptoCurrencyService;
import com.nikola.userhandlerbe2.services.UserService;
import com.nikola.userhandlerbe2.utils.CryptoCurrenciesFetcherService;
import com.nikola.userhandlerbe2.utils.LineChartMaker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@EqualsAndHashCode(callSuper = true)
@Data
public class CryptoProphetBot extends TelegramLongPollingBot {
    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    @Autowired
    private UserService userService;

    @Autowired
    private CryptoCurrenciesFetcherService cryptoCurrenciesFetcher;

    @Autowired
    private LineChartMaker lineChartMaker;

    boolean isRegistered = false;

    public CryptoProphetBot() {
        registerBot();
    }

    private void registerBot() {
        if (!isRegistered) {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(this);
                isRegistered = true;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
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
        if (!isRegistered) {
            registerBot();
        }
        var message = update.getMessage();
        var user = message.getFrom();
        System.out.println(message.getText());
        users.add(new UserDetails(user.getId(), null));
        System.out.println(user.getId());
        handleMessage(user.getId(), message.getText());
    }

    public void sendMessage(Long userId, String message) {
        if (!isRegistered) {
            registerBot();
        }
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

    public void sendImage(Long userId, File image) {
        if (!isRegistered) {
            registerBot();
        }

        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(userId.toString());
        // Set the photo url as a simple photo
        sendPhotoRequest.setPhoto(new InputFile(image.getFileId()));
        try {
            // Execute the method
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
                        /id - Show your telegram id so you can set it in the web page
                        /subscribe - Subscribe to a new cryptocurrency
                        /unsubscribe - Unsubscribe from a cryptocurrency
                        /charts - Show charts for a cryptocurrency
                        /news - Show news for a cryptocurrency
                        """);
            }
            case "/id" -> {
                sendMessage(userId, "Your telegram id is: " + userId);
            }
            case "/subscribe" -> {
                if (userService.isEnabled(userId.toString()) == null || !userService.isEnabled(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                sendMessage(userId, cryptoCurrencyService.addSubscribersToCryptoCurrency(message.split(" ")[1], userId));

            }
            case "/unsubscribe" -> {
                if (userService.isEnabled(userId.toString()) == null || !userService.isEnabled(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                sendMessage(userId, cryptoCurrencyService.removeSubscribersFromCryptoCurrency(message.split(" ")[1], userId));
            }
            case "/charts" -> {
                if (userService.isEnabled(userId.toString()) == null || !userService.isEnabled(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                switch (message.split(" ")[2]) {
                    case "1h" -> {
                        List<Double> values = cryptoCurrenciesFetcher.getPriceForPastHour(message.split(" ")[1]);
                        sendMessage(userId, lineChartMaker.plotLineChart(values));
                    }
                    case "24h" -> {
                        List<Double> values = cryptoCurrenciesFetcher.getPriceForPastDay(message.split(" ")[1]);
                        sendMessage(userId, lineChartMaker.plotLineChart(values));
                    }
                    case "7d" -> {
                        List<Double> values = cryptoCurrenciesFetcher.getPriceForPastWeek(message.split(" ")[1]);
                        sendMessage(userId, lineChartMaker.plotLineChart(values));
                    }
                    case "30d" -> {
                        List<Double> values = cryptoCurrenciesFetcher.getPriceForPastMonth(message.split(" ")[1]);
                        sendMessage(userId, lineChartMaker.plotLineChart(values));
                    }
                    case "1y" -> {
                        List<Double> values = cryptoCurrenciesFetcher.getPriceForPastYear(message.split(" ")[1]);
                        sendMessage(userId, lineChartMaker.plotLineChart(values));
                    }
                    default -> {
                        sendMessage(userId, "Unknown time period. Please use /help to see the list of available commands.");
                    }
                }
            }
            case "/news" -> {
                if (userService.isEnabled(userId.toString()) == null || !userService.isEnabled(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                //TODO call the news endpoint of the secured API
            }
            default -> {
                sendMessage(userId, "Unknown command. Please use /help to see the list of available commands.");
            }
        }
    }

}
