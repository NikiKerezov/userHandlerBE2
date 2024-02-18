package com.nikola.userhandlerbe2.services;

import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ArticleScraperService {

    public String scrapeArticle(String url) throws Exception {
        // Connect to the webpage
        Document document = Jsoup.connect(url).get();

        // Get the title of the webpage
        String title = document.title();
        System.out.println("Title: " + title);

        // Get the body of the webpage
        Element body = document.body();

        // Get all paragraphs
        Elements paragraphs = body.getElementsByTag("p");

        StringBuilder article = new StringBuilder();

        for (Element paragraph : paragraphs) {
            article.append(paragraph.text()).append("\n");
        }

        return article.toString();
    }
}