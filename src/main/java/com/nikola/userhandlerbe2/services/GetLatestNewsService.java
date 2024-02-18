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
    public List<String> getArticles(String name) throws IOException, InterruptedException {
        String apiLink = "https://newsdata.io/api/1/news?apikey=";
        String apiKey = "pub_3826784d349329e7c1351470ab145c048de97";
        String language = "en";

        String uri = apiLink + apiKey + "&language=" + language + "&q=" + name;
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
        List<String> articles = new ArrayList<>();
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
