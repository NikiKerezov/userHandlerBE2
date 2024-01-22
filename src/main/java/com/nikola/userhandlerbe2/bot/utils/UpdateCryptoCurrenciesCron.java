package com.nikola.userhandlerbe2.bot.utils;

import com.nikola.userhandlerbe2.bot.CryptoProphetBot;
import com.nikola.userhandlerbe2.entities.CryptoCurrency;
import com.nikola.userhandlerbe2.repositories.CryptoCurrencyRepository;
import com.nikola.userhandlerbe2.services.CryptoCurrencyService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateCryptoCurrenciesCron {
    private final CryptoCurrencyRepository cryptoCurrencyRepository;
    private final CryptoProphetBot cryptoProphetBot;

   public void updateCryptoCurrency(String name) {
        String coinGeckoApiUrl = "https://api.coingecko.com/api/v3/coins";
        String coinGeckoKey = "CG-RSdWXT3k95eCn2GunzFbT41A";

        Optional<CryptoCurrency> cryptoCurrency = cryptoCurrencyRepository.findByName(name);
        if (cryptoCurrency.isPresent()) {
            CryptoCurrency existingCryptoCurrency = cryptoCurrency.get();
            try {
                // Create a HTTP client
                HttpClient httpClient = HttpClient.newHttpClient();

                name = name.toLowerCase();

                // Create a GET request to the CoinGecko API and add the key to the header as authorization type bearer
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(coinGeckoApiUrl + "/" + name))
                        .header("Authorization", "Bearer " + coinGeckoKey)
                        .build();


                // Execute the GET request and get the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(response.body());

                double oldPrice = existingCryptoCurrency.getPrice();
                double newPrice = responseJson.getJSONObject("market_data").getJSONObject("current_price").getDouble("usd");

                // Update the existing cryptocurrency with the new data
                existingCryptoCurrency.setPrice(responseJson.getJSONObject("market_data").getJSONObject("current_price").getDouble("usd"));
                existingCryptoCurrency.setMarketCap(responseJson.getJSONObject("market_data").getJSONObject("market_cap").getDouble("usd"));
                existingCryptoCurrency.setVolume24h(responseJson.getJSONObject("market_data").getJSONObject("total_volume").getDouble("usd"));
                existingCryptoCurrency.setChange24h(responseJson.getJSONObject("market_data").getDouble("price_change_percentage_24h"));
                existingCryptoCurrency.setChange7d(responseJson.getJSONObject("market_data").getDouble("price_change_percentage_7d"));
                existingCryptoCurrency.setLastUpdated(new Date());


                System.out.println(existingCryptoCurrency.getSubscribersTelegramIds());
                System.out.println(existingCryptoCurrency.getPrice());

                //notify users
                double priceDifferencePercent = (newPrice - oldPrice) / oldPrice * 100;
                if (priceDifferencePercent > 5 || priceDifferencePercent < -5)
                    for (Long id : existingCryptoCurrency.getSubscribersTelegramIds()) {
                        cryptoProphetBot.sendMessage(id, "Price of " + existingCryptoCurrency.getName() + " has changed from " + oldPrice + " to " + newPrice + "$!");
                        cryptoProphetBot.sendMessage(id, "Price difference is " + priceDifferencePercent + "%");
                    }

                System.out.println(existingCryptoCurrency.getName() + " " + existingCryptoCurrency.getPrice());

                // Save the updated cryptocurrency
                cryptoCurrencyRepository.updateByName(
                        existingCryptoCurrency.getName(), existingCryptoCurrency.getPrice(),
                        existingCryptoCurrency.getMarketCap(), existingCryptoCurrency.getVolume24h(),
                        existingCryptoCurrency.getChange24h(), existingCryptoCurrency.getChange7d(),
                        existingCryptoCurrency.getLastUpdated(), existingCryptoCurrency.getSubscribersTelegramIds()
                );
            } catch (Exception e) {
                // Handle any exceptions that occur
                throw new RuntimeException("Failed to update crypto currency", e);
            }
        } else {
            try {
                // Create a new cryptocurrency
                CryptoCurrency newCryptoCurrency = new CryptoCurrency();
                newCryptoCurrency.setName(name);
                // Create a HTTP client
                HttpClient httpClient = HttpClient.newHttpClient();

                name = name.toLowerCase();

                // Create a GET request to the CoinGecko API
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(coinGeckoApiUrl + "/" + name))
                        .header("Authorization", "Bearer " + coinGeckoKey)
                        .build();

                // Execute the GET request and get the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(response.body());

                // Set the new cryptocurrency's data
                newCryptoCurrency.setPrice(responseJson.getJSONObject("market_data").getJSONObject("current_price").getDouble("usd"));
                newCryptoCurrency.setMarketCap(responseJson.getJSONObject("market_data").getJSONObject("market_cap").getDouble("usd"));
                newCryptoCurrency.setVolume24h(responseJson.getJSONObject("market_data").getJSONObject("total_volume").getDouble("usd"));
                newCryptoCurrency.setChange24h(responseJson.getJSONObject("market_data").getDouble("price_change_percentage_24h"));
                newCryptoCurrency.setChange7d(responseJson.getJSONObject("market_data").getDouble("price_change_percentage_7d"));
                newCryptoCurrency.setLastUpdated(new Date());
                newCryptoCurrency.setSubscribersTelegramIds(new ArrayList<>());

                System.out.println(newCryptoCurrency.getName() + " " + newCryptoCurrency.getPrice() + "\nNEW");

                // Save the new cryptocurrency
                cryptoCurrencyRepository.save(newCryptoCurrency);
            } catch (Exception e) {
                // Handle any exceptions that occur
                throw new RuntimeException("Failed to update crypto currency", e);
            }
        }
    }
    @Scheduled(fixedDelay = 60000)
    public void invokeUpdateCryptoCurrencies() {
//            updateCryptoCurrency("Bitcoin");
//            updateCryptoCurrency("Ethereum");
//            updateCryptoCurrency("Ripple");
//            updateCryptoCurrency("Tether");
//            updateCryptoCurrency("Cardano");
    }
}
