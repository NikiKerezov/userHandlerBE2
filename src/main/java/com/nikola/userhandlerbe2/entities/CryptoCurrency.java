package com.nikola.userhandlerbe2.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Document(collection = "cryptocurrencies")
public class CryptoCurrency {
    private String name;
    @Id
    private String symbol;
    private double price;
    private double marketCap;
    private double volume24h;
    private double change24h;
    private double change7d;
    private Date lastUpdated;

    private List<Long> subscribersTelegramIds;
}
