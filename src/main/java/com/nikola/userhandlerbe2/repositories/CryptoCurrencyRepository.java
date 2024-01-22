package com.nikola.userhandlerbe2.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.nikola.userhandlerbe2.entities.CryptoCurrency;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoCurrencyRepository extends MongoRepository<CryptoCurrency, String> {
    Optional<CryptoCurrency> findBySymbol(String symbol);
    Optional<CryptoCurrency> findByName(String name);

    @Modifying
    @Query("{'name': ?0, 'price': ?1, 'marketCap': ?2, 'volume24h': ?3, 'change24h': ?4, 'change7d': ?5, 'lastUpdated': ?6, 'subscribersTelegramIds': ?7}")
    void updateByName(String name, double price, double marketCap, double volume24h, double change24h, double change7d, Date lastUpdated, List<Long> subscribersTelegramIds);

}
