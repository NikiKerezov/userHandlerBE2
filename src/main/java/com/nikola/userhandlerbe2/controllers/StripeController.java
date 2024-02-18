package com.nikola.userhandlerbe2.controllers;

import com.nikola.userhandlerbe2.services.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {
    private final StripeService stripeHookService;
    @PostMapping("/hook")
    public void handleStripeHook(@RequestBody String payload) {
        stripeHookService.handleHook(payload);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/cancel-subscription")
    public String cancelSubscription(@RequestBody String username) {
        try {
            return stripeHookService.cancelSubscription(username);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
