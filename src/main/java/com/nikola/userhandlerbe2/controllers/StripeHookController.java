package com.nikola.userhandlerbe2.controllers;

import com.nikola.userhandlerbe2.services.StripeHookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeHookController {
    private final StripeHookService stripeHookService;
    @PostMapping("/hook")
    public void handleStripeHook(@RequestBody String payload) {
        stripeHookService.handle(payload);
    }
}
