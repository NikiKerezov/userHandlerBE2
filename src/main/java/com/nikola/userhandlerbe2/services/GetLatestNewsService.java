package com.nikola.userhandlerbe2.services;

import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;

@Service
@NoArgsConstructor
public class GetLatestNewsService {
    @Value("${newsDataApiKey}")
    private String newsDataApiKey;

    @Value("${newsDataApiUrl}")
    private String newsDataApiUrl;

    public HashSet<String> getArticles(String name) throws IOException, InterruptedException {
        String language = "en";

        String uri = newsDataApiUrl + newsDataApiKey + "&language=" + language + "&q=" + name;
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
        HashSet<String> articles = new HashSet<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject article = results.getJSONObject(i);
            JSONArray keywords;
            try {
                keywords = article.getJSONArray("keywords");
            } catch (Exception e) {
                continue;
            }
            String title = article.getString("title").toLowerCase();
            if (articles.size() == 4) {
                break;
            }
            if (title.contains(name.toLowerCase())) {
                articles.add(article.getString("link"));
            } else {
                keywords.forEach(keyword -> {
                    if (keyword.toString().toLowerCase().contains(name.toLowerCase())) {
                        articles.add(article.getString("link"));
                    }
                });
            }
        }

        if (articles.isEmpty()) {
            throw new RuntimeException("No articles found for the given keyword");
        }

        return articles;
    }
}
