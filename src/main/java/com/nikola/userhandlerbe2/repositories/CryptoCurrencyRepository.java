package com.nikola.userhandlerbe2.repositories;

import java.util.Optional;
import com.nikola.userhandlerbe2.entities.CryptoCurrency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoCurrencyRepository extends MongoRepository<CryptoCurrency, String> {
    Optional<CryptoCurrency> findBySymbol(String symbol);
    Optional<CryptoCurrency> findByName(String name);


}
