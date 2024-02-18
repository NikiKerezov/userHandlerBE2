package com.nikola.userhandlerbe2.services;

import com.nikola.userhandlerbe2.entities.User;
import com.nikola.userhandlerbe2.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void setSubscriptionId(String email, String subscriptionId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setSubscriptionId(subscriptionId);
        userRepository.save(user);
    }

    public void setTelegramId(String username, String telegramId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setTelegramId(telegramId);
        userRepository.save(user);
    }

    public void setEnabled(String email, boolean enabled) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    public Boolean isEnabledByTgId(String telegramId) {
        try {
            User user = userRepository.findByTelegramId(telegramId).orElseThrow(() -> new IllegalArgumentException("User not found"));
            return user.getEnabled();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Boolean isEnabledByUsername(String username) {
        try {
            username = username.replace("=", "");
            User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
            return user.getEnabled();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
