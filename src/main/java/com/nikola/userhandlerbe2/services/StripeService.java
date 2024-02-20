package com.nikola.userhandlerbe2.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikola.userhandlerbe2.utils.CustomerDetailsExtractor;
import com.nikola.userhandlerbe2.utils.Logger;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCancelParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {
    private final CustomerDetailsExtractor customerDetailsExtractor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    @Value("${stripe.api.key}")
    private String apiKey;
    public String cancelSubscription(String username) throws StripeException {
        username = username.replace("=", "");
        String subscriptionId = userService.getUserByUsername(username).getSubscriptionId();
        String email = userService.getUserByUsername(username).getEmail();
        if (subscriptionId == null) {
            return "No subscription found";
        }

        StripeClient client = new StripeClient(apiKey);

        SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();

        Subscription subscription = client.subscriptions().cancel(subscriptionId, params);

        userService.setEnabled(email, false);
        return "Subscription cancelled";
    }

    public void handleHook(String payload) {
        Event event = null;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            event = objectMapper.readValue(payload, Event.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Deserialize the nested object inside the event
        StripeObject stripeObject = null;
        if (event.getDataObjectDeserializer().getObject().isPresent()) {
            stripeObject = event.getDataObjectDeserializer().getObject().get();
        } else {
            System.out.println("No data object present in the event.");
        }

        // Handle the event
        switch (event.getType()) {
            case "checkout.session.completed":
                Logger.log("Checkout session completed" + "\n"
                        + "Customer name: " + customerDetailsExtractor.extractCustomerName(payload)
                        + "\n" + "Customer email: " + customerDetailsExtractor.extractCustomerEmail(payload)
                        + "\n" + "Payment status: " + customerDetailsExtractor.extractPaymentStatus(payload));
                userService.setEnabled(customerDetailsExtractor.extractCustomerEmail(payload), true);
                userService.setSubscriptionId(customerDetailsExtractor.extractCustomerEmail(payload), customerDetailsExtractor.extractSubscriptionId(payload));
                break;
            case "customer.subscription.deleted":
                Logger.log("Subscription deleted" + "\n"
                        + "Customer name: " + customerDetailsExtractor.extractCustomerName(payload)
                        + "\n" + "Customer email: " + customerDetailsExtractor.extractCustomerEmail(payload)
                        + "\n" + "Payment status: " + customerDetailsExtractor.extractPaymentStatus(payload));
                userService.setEnabled(customerDetailsExtractor.extractCustomerEmail(payload), false);
                break;
            case "invoice.payment_failed":
                Logger.log("Payment failed" + "\n"
                        + "Customer name: " + customerDetailsExtractor.extractCustomerName(payload)
                        + "\n" + "Customer email: " + customerDetailsExtractor.extractCustomerEmail(payload)
                        + "\n" + "Payment status: " + customerDetailsExtractor.extractPaymentStatus(payload));
                userService.setEnabled(customerDetailsExtractor.extractCustomerEmail(payload), false);
                break;
        }
    }
}
