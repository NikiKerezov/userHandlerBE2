package com.nikola.userhandlerbe2.bot;

import com.nikola.userhandlerbe2.services.*;
import com.nikola.userhandlerbe2.utils.LineChartMaker;
import com.nikola.userhandlerbe2.utils.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Set;

@Component
@EqualsAndHashCode(callSuper = true)
@Data
public class CryptoProphetBot extends TelegramLongPollingBot {
    private final CryptoCurrencyService cryptoCurrencyService;

    private final UserService userService;

    private final CryptoCurrenciesFetcherService cryptoCurrenciesFetcher;

    private final LineChartMaker lineChartMaker;

    private final VertexAiPrompterService vertexAiPrompter;
    private final ArticleScraperService articleScraperService;
    private final GetLatestNewsService getLatestNewsService;
    private boolean isRegistered = false;
//    @Value("${bot.token}")
//    private String botToken;

    public CryptoProphetBot(CryptoCurrencyService cryptoCurrencyService, UserService userService, CryptoCurrenciesFetcherService cryptoCurrenciesFetcher, LineChartMaker lineChartMaker, VertexAiPrompterService vertexAiPrompter, ArticleScraperService articleScraperService, GetLatestNewsService getLatestNewsService) {
        registerBot();
        this.cryptoCurrencyService = cryptoCurrencyService;
        this.userService = userService;
        this.cryptoCurrenciesFetcher = cryptoCurrenciesFetcher;
        this.lineChartMaker = lineChartMaker;
        this.vertexAiPrompter = vertexAiPrompter;
        this.articleScraperService = articleScraperService;
        this.getLatestNewsService = getLatestNewsService;
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
        Logger.log("Received message from " + user.getId() + ": " + message.getText());
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
            Logger.log("Sending message to " + userId + ": " + message);
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
                if (message.split(" ").length > 1) {
                    switch (message.split(" ")[1]) {
                        case "start" -> sendMessage(userId, "Start the bot");
                        case "help" -> sendMessage(userId, "Show the list of available commands");
                        case "id" -> sendMessage(userId, "Show your telegram id so you can set it in the web page");
                        case "coin" -> sendMessage(userId, "Show information for a cryptocurrency\n" +
                                "Usage: /coin [cryptocurrency]\n" +
                                "Example: /coin bitcoin\n" +
                                "Disclaimer: Write the full name of the cryptocurrency!");
                        case "subscribe" -> sendMessage(userId, "Subscribe to a new cryptocurrency\n" +
                                "Usage: /subscribe [cryptocurrency]\n" +
                                "Example: /subscribe bitcoin\n" +
                                "Disclaimer: Write the full name of the cryptocurrency!");
                        case "unsubscribe" -> sendMessage(userId, "Unsubscribe from a cryptocurrency\n" +
                                "Usage: /unsubscribe [cryptocurrency]\n" +
                                "Example: /unsubscribe bitcoin\n" +
                                "Disclaimer: Write the full name of the cryptocurrency!");
                        case "charts" -> sendMessage(userId, "Show charts for a cryptocurrency\n" +
                                "Usage: /charts [cryptocurrency] [time period]\n" +
                                "Example: /charts bitcoin 1h\n" +
                                "Time periods: 1h, 24h, 7d, 30d, 1y\n" +
                                "Disclaimer: Write the full name of the cryptocurrency and one of the listed time periods!");
                        case "news" -> sendMessage(userId, "Show news for a cryptocurrency\n" +
                                "Usage: /news [cryptocurrency]\n" +
                                "Example: /news bitcoin\n" +
                                "Disclaimer: Write the full name of the cryptocurrency!");
                        default -> sendMessage(userId, "Unknown command. Please use /help to see the list of available commands.");
                    }
                } else {
                    sendMessage(userId, """
                            /start - Start the bot
                            /help - Show the list of available commands
                            /id - Show your telegram id so you can set it in the web page
                            /coin - Show supported cryptocurrencies to subscribe to / Show information for a cryptocurrency
                            /subscribe - Subscribe to a new cryptocurrency 
                            /unsubscribe - Unsubscribe from a cryptocurrency 
                            /charts - Show charts for a cryptocurrency 
                            /news - Show news for a cryptocurrency
                            For a detailed subscription of a command, use /help [command] 
                            """);
                }
            }
            case "/id" -> {
                sendMessage(userId, "Your telegram id is: " + userId);
            }
            case "/coin" -> {
                if (userService.isEnabledByTgId(userId.toString()) == null || !userService.isEnabledByTgId(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                if (message.split(" ").length > 1) {
                    try {
                        sendMessage(userId, cryptoCurrencyService.getCryptoCurrency(message.split(" ")[1]));
                    } catch (Exception e) {
                        sendMessage(userId, "Failed to get cryptocurrency. There is likely a problem with the command format :(");
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        sendMessage(userId, cryptoCurrencyService.getSupportedCryptoCurrencies());
                    } catch (Exception e) {
                        sendMessage(userId, "Failed to get cryptocurrency. There is likely a problem with the command format :(");
                        throw new RuntimeException(e);
                    }
                }
            }
            case "/subscribe" -> {
                if (userService.isEnabledByTgId(userId.toString()) == null || !userService.isEnabledByTgId(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                try {
                    sendMessage(userId, cryptoCurrencyService.addSubscribersToCryptoCurrency(message.split(" ")[1], userId));
                } catch (Exception e) {
                    sendMessage(userId, "Failed to subscribe to the cryptocurrency. There is likely a problem with the command format :(");
                    throw new RuntimeException(e);
                }
            }
            case "/unsubscribe" -> {
                if (userService.isEnabledByTgId(userId.toString()) == null || !userService.isEnabledByTgId(userId.toString())) {
                    sendMessage(userId, "You need to be logged in and subscribed to use this command. Please log in and subscribe in the web page.");
                    return;
                }
                try {
                    sendMessage(userId, cryptoCurrencyService.removeSubscribersFromCryptoCurrency(message.split(" ")[1], userId));
                } catch (Exception e) {
                    sendMessage(userId, "Failed to unsubscribe from the cryptocurrency. There is likely a problem with the command format :(");
                    throw new RuntimeException(e);
                }
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
                            sendMessage(userId, "Failed to get price for past day. If you typed the command correctly, my calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    case "7d" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastWeek(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past week. If you typed the command correctly, my calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    case "30d" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastMonth(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past month. If you typed the command correctly, my calls have probably ran out. Try in a minute! :)");
                            throw new RuntimeException(e);
                        }
                    }
                    case "1y" -> {
                        try {
                            List<Double> values = cryptoCurrenciesFetcher.getPriceForPastYear(message.split(" ")[1]);
                            sendMessage(userId, lineChartMaker.plotLineChart(values));
                        } catch (Exception e) {
                            sendMessage(userId, "Failed to get price for past year. If you typed the command correctly, my calls have probably ran out. Try in a minute! :)");
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

                Set<String> articles;

                try {
                    articles = getLatestNewsService.getArticles(message.split(" ")[1]);
                    //check for duplicates

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
                    sendMessage(userId, "Disclaimer: If the sentiments are less that the articles, it means that some of the articles are faulty or too long for the Vertex AI to process!");
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
