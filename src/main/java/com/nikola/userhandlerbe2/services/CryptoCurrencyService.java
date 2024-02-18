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

   public String addSubscribersToCryptoCurrency(String currencyName, Long telegramId) {
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
}