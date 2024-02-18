package com.nikola.userhandlerbe2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cryptocurrencies")
public class CryptoCurrency {
    @Id
    private String name;

    private String symbol;
    private double price;
    private double marketCap;
    private double volume24h;
    private double change24h;
    private double change7d;
    private Date lastUpdated;

    @Field("subscribersTelegramIds")
    private List<Long> subscribersTelegramIds;

    public void addSubscriber(Long subscriberTelegramId) {
        if (subscribersTelegramIds == null) {
            subscribersTelegramIds = new ArrayList<>();
        }

        if (!subscribersTelegramIds.contains(subscriberTelegramId)) {
            subscribersTelegramIds.add(subscriberTelegramId);
        } else {
            System.out.println("User already subscribed to this cryptocurrency.");
        }
    }

    public void removeSubscriber(Long userId) {
        if (subscribersTelegramIds != null) {
            subscribersTelegramIds.remove(userId);
        }
    }
}
