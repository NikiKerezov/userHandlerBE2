package com.nikola.userhandlerbe2.services;


import com.nikola.userhandlerbe2.entities.CryptoCurrency;
import com.nikola.userhandlerbe2.repositories.CryptoCurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CryptoCurrencyService {
   private final CryptoCurrencyRepository cryptoCurrencyRepository;

   public String getCryptoCurrency(String currencyName) {
       currencyName = currencyName.toLowerCase();
       Optional<CryptoCurrency> cryptoCurrency = cryptoCurrencyRepository.findByName(currencyName);
       if (cryptoCurrency.isPresent()) {

                return "Name: " + cryptoCurrency.get().getName() + "\n" +
                   "Price: " + cryptoCurrency.get().getPrice() + "\n" +
                   "Market Cap: " + cryptoCurrency.get().getMarketCap() + "\n" +
                   "Volume24h: " + cryptoCurrency.get().getVolume24h() + "\n" +
                   "Change24h: " + cryptoCurrency.get().getChange24h() + "\n" +
                   "Change7d: " + cryptoCurrency.get().getChange7d() + "\n" +
                   "Last Updated: " + cryptoCurrency.get().getLastUpdated() + "\n";
       }
       return "There is no such cryptocurrency as " + currencyName + ", that is supported!";
   }

   public String addSubscribersToCryptoCurrency(String currencyName, Long telegramId) {
       currencyName = currencyName.toLowerCase();
       Optional<CryptoCurrency> cryptoCurrency = cryptoCurrencyRepository.findByName(currencyName);
       if (cryptoCurrency.isPresent() && !cryptoCurrency.get().getSubscribersTelegramIds().contains(telegramId)) {
           CryptoCurrency existingCryptoCurrency = cryptoCurrency.get();
           existingCryptoCurrency.addSubscriber(telegramId);
           cryptoCurrencyRepository.save(existingCryptoCurrency);
           return "You have successfully subscribed to " + currencyName + "!";
       } else if (cryptoCurrency.isEmpty()) {
           return "There is no such cryptocurrency as " + currencyName + ", that is supported!";
       }

         return "You are already subscribed to " + currencyName + "!";
   }

    public String removeSubscribersFromCryptoCurrency(String currencyName, Long telegramId) {
         currencyName = currencyName.toLowerCase();
         Optional<CryptoCurrency> cryptoCurrency = cryptoCurrencyRepository.findByName(currencyName);
         if (cryptoCurrency.isPresent() && cryptoCurrency.get().getSubscribersTelegramIds().contains(telegramId)) {
              CryptoCurrency existingCryptoCurrency = cryptoCurrency.get();
              existingCryptoCurrency.removeSubscriber(telegramId);
              cryptoCurrencyRepository.save(existingCryptoCurrency);
                return "You have successfully unsubscribed from " + currencyName + "!";
         } else if (cryptoCurrency.isEmpty()) {
             return "There is no such cryptocurrency as " + currencyName + ", that is supported!";
         }
            return "You are not subscribed to " + currencyName + "!";
    }

    public String getSupportedCryptoCurrencies() {
        List<CryptoCurrency> cryptoCurrencies = cryptoCurrencyRepository.findAll();
        StringBuilder supportedCryptoCurrencies = new StringBuilder();
        for (CryptoCurrency cryptoCurrency : cryptoCurrencies) {
            supportedCryptoCurrencies.append(cryptoCurrency.getName()).append("\n");
        }
        return supportedCryptoCurrencies.toString();
    }
}