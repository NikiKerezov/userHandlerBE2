package com.nikola.userhandlerbe2.services;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoCurrenciesFetcherService {
    @Value("${coinGecko.api.url}")
    private String coinGeckoApiUrl;
    @Value("${coinGecko.api.key}")
    private String coinGeckoKey;
    public List<Double> getPriceForPastHour(String name) {
        try {
            // Create a HTTP client
            HttpClient httpClient = HttpClient.newHttpClient();

            name = name.toLowerCase();

            // Create a GET request to the CoinGecko API and add the key to the header as authorization type bearer
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(coinGeckoApiUrl + "/" + name + "/market_chart?vs_currency=usd&days=1"))
                    .header("Authorization", "Bearer " + coinGeckoKey)
                    .build();

            // Execute the GET request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject responseJson = new JSONObject(response.body());

            List<Double> prices = new ArrayList<>();
            JSONArray pricesArray = responseJson.getJSONArray("prices");
            for (int i = 0; i < pricesArray.length(); i++) {
                JSONArray pricePoint = pricesArray.getJSONArray(i);
                double price = pricePoint.getDouble(1);
                prices.add(price);
            }
            return prices;
        } catch (Exception e) {
            // Handle any exceptions that occur
            throw new RuntimeException("Failed to get price for past hour", e);
        }
    }

    public List<Double> getPriceForPastDay(String name) {
        try {
            // Create a HTTP client
            HttpClient httpClient = HttpClient.newHttpClient();

            name = name.toLowerCase();

            // Create a GET request to the CoinGecko API and add the key to the header as authorization type bearer
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(coinGeckoApiUrl + "/" + name + "/market_chart?vs_currency=usd&days=1"))
                    .header("Authorization", "Bearer " + coinGeckoKey)
                    .build();

            // Execute the GET request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject responseJson = new JSONObject(response.body());
            List<Double> prices = new ArrayList<>();
            JSONArray pricesArray = responseJson.getJSONArray("prices");
            for (int i = 0; i < pricesArray.length(); i++) {
                JSONArray pricePoint = pricesArray.getJSONArray(i);
                double price = pricePoint.getDouble(1);
                prices.add(price);
            }
            return prices;
        } catch (Exception e) {
            // Handle any exceptions that occur
            throw new RuntimeException("Failed to get price for past day", e);
        }

    }

    public List<Double> getPriceForPastWeek(String name) {
        try {
            // Create a HTTP client
            HttpClient httpClient = HttpClient.newHttpClient();

            name = name.toLowerCase();

            // Create a GET request to the CoinGecko API and add the key to the header as authorization type bearer
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(coinGeckoApiUrl + "/" + name + "/market_chart?vs_currency=usd&days=7"))
                    .header("Authorization", "Bearer " + coinGeckoKey)
                    .build();

            // Execute the GET request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject responseJson = new JSONObject(response.body());
            List<Double> prices = new ArrayList<>();
            JSONArray pricesArray = responseJson.getJSONArray("prices");
            for (int i = 0; i < pricesArray.length(); i++) {
                JSONArray pricePoint = pricesArray.getJSONArray(i);
                double price = pricePoint.getDouble(1);
                prices.add(price);
            }
            return prices;
        } catch (Exception e) {
            // Handle any exceptions that occur
            throw new RuntimeException("Failed to get price for past week", e);
        }
    }

    public List<Double> getPriceForPastMonth(String name) {
        try {
            // Create a HTTP client
            HttpClient httpClient = HttpClient.newHttpClient();

            name = name.toLowerCase();

            // Create a GET request to the CoinGecko API and add the key to the header as authorization type bearer
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(coinGeckoApiUrl + "/" + name + "/market_chart?vs_currency=usd&days=30"))
                    .header("Authorization", "Bearer " + coinGeckoKey)
                    .build();

            // Execute the GET request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject responseJson = new JSONObject(response.body());
            List<Double> prices = new ArrayList<>();
            JSONArray pricesArray = responseJson.getJSONArray("prices");
            for (int i = 0; i < pricesArray.length(); i++) {
                JSONArray pricePoint = pricesArray.getJSONArray(i);
                double price = pricePoint.getDouble(1);
                prices.add(price);
            }
            return prices;
        } catch (Exception e) {
            // Handle any exceptions that occur
            throw new RuntimeException("Failed to get price for past month", e);
        }
    }

    public List<Double> getPriceForPastYear(String name) {
        try {
            // Create a HTTP client
            HttpClient httpClient = HttpClient.newHttpClient();

            name = name.toLowerCase();

            // Create a GET request to the CoinGecko API and add the key to the header as authorization type bearer
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(coinGeckoApiUrl + "/" + name + "/market_chart?vs_currency=usd&days=365"))
                    .header("Authorization", "Bearer " + coinGeckoKey)
                    .build();

            // Execute the GET request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject responseJson = new JSONObject(response.body());
            List<Double> prices = new ArrayList<>();
            JSONArray pricesArray = responseJson.getJSONArray("prices");
            for (int i = 0; i < pricesArray.length(); i++) {
                JSONArray pricePoint = pricesArray.getJSONArray(i);
                double price = pricePoint.getDouble(1);
                prices.add(price);
            }
            return prices;
        } catch (Exception e) {
            // Handle any exceptions that occur
            throw new RuntimeException("Failed to get price for past year", e);
        }
    }
}
