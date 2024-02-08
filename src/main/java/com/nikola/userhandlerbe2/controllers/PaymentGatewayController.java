package com.nikola.userhandlerbe2.controllers;

import com.nikola.userhandlerbe2.utils.StripeClient;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentGatewayController {
    private final StripeClient client;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/checkout")
    public Charge chargeCard(@RequestHeader(value="token") String token, @RequestHeader(value="amount") Double amount) throws Exception {
        return this.client.chargeNewCard(token, amount);
    }
}