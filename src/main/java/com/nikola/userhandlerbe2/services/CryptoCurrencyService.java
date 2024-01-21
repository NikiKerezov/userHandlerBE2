package com.nikola.userhandlerbe2.services;

import com.nikola.userhandlerbe2.bot.CryptoProphetBot;
import com.nikola.userhandlerbe2.entities.CryptoCurrency;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.nikola.userhandlerbe2.repositories.CryptoCurrencyRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CryptoCurrencyService {
   private final CryptoCurrencyRepository cryptoCurrencyRepository;

   public void updateCryptoCurrency(String symbol) {
        String coinGeckoApiUrl = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=USD";

        Optional<CryptoCurrency> cryptoCurrency = cryptoCurrencyRepository.findBySymbol(symbol);
        if (cryptoCurrency.isPresent()) {
            CryptoCurrency existingCryptoCurrency = cryptoCurrency.get();
            try {
                // Create a HTTP client
                HttpClient httpClient = HttpClient.newHttpClient();

                // Create a GET request to the CoinGecko API
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(coinGeckoApiUrl + "&ids=" + symbol))
                        .build();

                // Execute the GET request and get the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(response.body());

                double oldPrice = existingCryptoCurrency.getPrice();
                double newPrice = responseJson.getJSONObject("data").getDouble("price");

                if (oldPrice != newPrice) {
                    // Send a message to all subscribers
                    for (Long subscriberTelegramId : existingCryptoCurrency.getSubscribersTelegramIds()) {
                        CryptoProphetBot.getInstance().sendMessage(subscriberTelegramId, "The price of " + symbol + " has changed from " + oldPrice + " to " + newPrice);
                    }
                }

                // Update the existing cryptocurrency with the new data
                existingCryptoCurrency.setPrice(responseJson.getJSONObject("data").getDouble("price"));
                existingCryptoCurrency.setMarketCap(responseJson.getJSONObject("data").getDouble("market_cap"));
                existingCryptoCurrency.setVolume24h(responseJson.getJSONObject("data").getDouble("volume_24h"));
                existingCryptoCurrency.setChange24h(responseJson.getJSONObject("data").getDouble("change_24h"));
                existingCryptoCurrency.setChange7d(responseJson.getJSONObject("data").getDouble("change_7d"));
                existingCryptoCurrency.setLastUpdated(new Date());


                System.out.println(existingCryptoCurrency.getSubscribersTelegramIds());
                System.out.println(existingCryptoCurrency.getPrice());
                // Save the updated cryptocurrency
                cryptoCurrencyRepository.save(existingCryptoCurrency);
            } catch (Exception e) {
                // Handle any exceptions that occur
                throw new RuntimeException("Failed to update crypto currency", e);
            }
        } else {
            try {
                // Create a new cryptocurrency
                CryptoCurrency newCryptoCurrency = new CryptoCurrency();
                newCryptoCurrency.setSymbol(symbol);
                // Create a HTTP client
                HttpClient httpClient = HttpClient.newHttpClient();

                // Create a GET request to the CoinGecko API
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(coinGeckoApiUrl + "&ids=" + symbol))
                        .build();

                // Execute the GET request and get the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(response.body());

                // Set the new cryptocurrency's data
                newCryptoCurrency.setPrice(responseJson.getJSONObject("data").getDouble("price"));
                newCryptoCurrency.setMarketCap(responseJson.getJSONObject("data").getDouble("market_cap"));
                newCryptoCurrency.setVolume24h(responseJson.getJSONObject("data").getDouble("volume_24h"));
                newCryptoCurrency.setChange24h(responseJson.getJSONObject("data").getDouble("change_24h"));
                newCryptoCurrency.setChange7d(responseJson.getJSONObject("data").getDouble("change_7d"));
                newCryptoCurrency.setLastUpdated(new Date());

                // Save the new cryptocurrency
                cryptoCurrencyRepository.save(newCryptoCurrency);
            } catch (Exception e) {
                // Handle any exceptions that occur
                throw new RuntimeException("Failed to update crypto currency", e);
            }
        }
    }
}
