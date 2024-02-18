package com.nikola.userhandlerbe2.bot;

import com.nikola.userhandlerbe2.services.*;
import com.nikola.userhandlerbe2.utils.LineChartMaker;
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

    @Autowired
    private VertexAiPrompterService vertexAiPrompter;
    @Autowired
    private ArticleScraperService articleScraperService;
    @Autowired
    private GetLatestNewsService getLatestNewsService;
    private boolean isRegistered = false;
    private Set<Long> users = new HashSet<>();

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
        users.add(user.getId());
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
                        /subscribe - Subscribe to a new cryptocurrency \n-> /subscribe <cryptocurrency>
                        /unsubscribe - Unsubscribe from a cryptocurrency \n-> /unsubscribe <cryptocurrency>
                        /charts - Show charts for a cryptocurrency \n-> /charts <cryptocurrency> <time period> \n-> Time periods: 1h, 24h, 7d, 30d, 1y
                        /news - Show news for a cryptocurrency \n-> /news <cryptocurrency>
                        """);
            }
            case "/id" -> {
                sendMessage(userId, "Your telegram id is: " + userId);
            }
            case "/subscribe" -> {
                if (userService.isEnabledByTgId(userId.toString()) == null || !userService.isEnabledByTgId(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                sendMessage(userId, cryptoCurrencyService.addSubscribersToCryptoCurrency(message.split(" ")[1], userId));

            }
            case "/unsubscribe" -> {
                if (userService.isEnabledByTgId(userId.toString()) == null || !userService.isEnabledByTgId(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                sendMessage(userId, cryptoCurrencyService.removeSubscribersFromCryptoCurrency(message.split(" ")[1], userId));
            }
            case "/charts" -> {
                if (userService.isEnabledByTgId(userId.toString()) == null || !userService.isEnabledByTgId(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                switch (message.split(" ")[2]) {
                    case "1h" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastHour(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past hour. My calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    case "24h" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastDay(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past day. My calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    case "7d" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastWeek(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past week. My calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    case "30d" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastMonth(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past month. My calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    case "1y" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastYear(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past year. My calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    default -> {
                        sendMessage(userId, "Unknown time period. Please use /help to see the list of available commands.");
                    }
                }
            }
            case "/news" -> {
                if (userService.isEnabledByTgId(userId.toString()) == null || !userService.isEnabledByTgId(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }

                List<String> articles;

                try {
                    articles = getLatestNewsService.getArticles(message.split(" ")[1]);
                    sendMessage(userId, "Here are the latest news for " + message.split(" ")[1] + " :D");

                    for (String article : articles) {
                        sendMessage(userId, article);
                    }
                } catch (Exception e) {
                    sendMessage(userId, "Failed to get news for the cryptocurrency / no articles found :(");
                    throw new RuntimeException(e);
                }

                try {
                    StringBuilder articlesText = new StringBuilder();
                    for (String article : articles) {
                        articlesText.append("\nARTICLE\n").append(articleScraperService.scrapeArticle(article)).append("\n");
                    }

                    sendMessage(userId, "Here are the sentiments on these articles :)");
                    sendMessage(userId, vertexAiPrompter.getSentiments(message.split(" ")[1], String.valueOf(articlesText)));
                } catch (Exception e) {
                    sendMessage(userId, "Failed to get sentiments for the articles. There is likely a problem with the Json formatting or the Vertex API :(");
                    throw new RuntimeException(e);
                }
            }
            default -> {
                sendMessage(userId, "Unknown command. Please use /help to see the list of available commands.");
            }
        }
    }

}
