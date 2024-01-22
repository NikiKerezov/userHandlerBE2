package com.nikola.userhandlerbe2.services;


import com.nikola.userhandlerbe2.entities.CryptoCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.nikola.userhandlerbe2.repositories.CryptoCurrencyRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CryptoCurrencyService {
   private final CryptoCurrencyRepository cryptoCurrencyRepository;

   public void addSubscribersToCryptoCurrency(String currencyName, Long telegramId) {
       Optional<CryptoCurrency> cryptoCurrency = cryptoCurrencyRepository.findByName(currencyName);
       if (cryptoCurrency.isPresent()) {
           CryptoCurrency existingCryptoCurrency = cryptoCurrency.get();
           existingCryptoCurrency.addSubscriber(telegramId);
           existingCryptoCurrency.setPrice(9999);
           cryptoCurrencyRepository.updateByName(existingCryptoCurrency.getName(), existingCryptoCurrency.getPrice(), existingCryptoCurrency.getMarketCap(), existingCryptoCurrency.getVolume24h(), existingCryptoCurrency.getChange24h(), existingCryptoCurrency.getChange7d(), existingCryptoCurrency.getLastUpdated(), existingCryptoCurrency.getSubscribersTelegramIds());
           CryptoCurrency test = cryptoCurrencyRepository.findByName(currencyName).get();
           System.out.println(test.getSubscribersTelegramIds());
           System.out.println(test.getPrice());
       }
   }

    public void removeSubscribersFromCryptoCurrency(String currencyName, Long telegramId) {
         Optional<CryptoCurrency> cryptoCurrency = cryptoCurrencyRepository.findByName(currencyName);
         if (cryptoCurrency.isPresent()) {
              CryptoCurrency existingCryptoCurrency = cryptoCurrency.get();
              existingCryptoCurrency.removeSubscriber(telegramId);
              cryptoCurrencyRepository.updateByName(existingCryptoCurrency.getName(), existingCryptoCurrency.getPrice(), existingCryptoCurrency.getMarketCap(), existingCryptoCurrency.getVolume24h(), existingCryptoCurrency.getChange24h(), existingCryptoCurrency.getChange7d(), existingCryptoCurrency.getLastUpdated(), existingCryptoCurrency.getSubscribersTelegramIds());
              CryptoCurrency test = cryptoCurrencyRepository.findByName(currencyName).get();
         }
    }
}