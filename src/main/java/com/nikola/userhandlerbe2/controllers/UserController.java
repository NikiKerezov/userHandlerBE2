package com.nikola.userhandlerbe2.controllers;

import com.nikola.userhandlerbe2.requests.SetTelegramIdRequest;
import com.nikola.userhandlerbe2.services.UserService;
import com.nikola.userhandlerbe2.utils.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userHandler/v2/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RequestMapping("/setTelegramId")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> setTelegramId(@RequestBody SetTelegramIdRequest request) {
        String username = request.getUsername();
        String telegramId = request.getTelegramId();
        try {
            userService.setTelegramId(username, telegramId);
            Logger.log("Telegram ID set for user " + username + " to " + telegramId);
            return ResponseEntity.ok("Telegram ID set successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping("/isProfileEnabled")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Boolean> isEnabled(@RequestBody String username) {
        return ResponseEntity.ok(userService.isEnabledByUsername(username));
    }
}
