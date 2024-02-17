package com.nikola.userhandlerbe2.services;

import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class GetLatestNewsService {
    public List<String> getArticleLinks(String cryptoCurrencyName) throws IOException, InterruptedException, RuntimeException {
    String apiLink = "https://newsdata.io/api/1/news?apikey=";
    String apiKey = "pub_3826784d349329e7c1351470ab145c048de97";
    String language = "en";

    String uri = apiLink + apiKey + "&language=" + language + "&q=" + cryptoCurrencyName;
    HttpClient httpClient = HttpClient.newHttpClient();

    // Create a GET request to the NewsData API
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();

    // Execute the GET request and get the response
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    // Parse the JSON response
    JSONObject responseJson = new JSONObject(response.body());

    JSONArray results = responseJson.getJSONArray("results");
    List<String> articleLinks = new ArrayList<>();
    for (int i = 0; i < results.length(); i++) {
        JSONObject article = results.getJSONObject(i);
        JSONArray keywords = article.getJSONArray("keywords");
        for (int j = 0; j < keywords.length(); j++) {
            if (keywords.getString(j).equalsIgnoreCase("ethereum")) {
                articleLinks.add(article.getString("link"));
                if (articleLinks.size() == 5) {
                    return articleLinks;
                }
                break;
            }
        }
    }

    if (articleLinks.isEmpty()) {
        throw new RuntimeException("No articles found for the given cryptocurrency");
    }

    return articleLinks;
}
}
