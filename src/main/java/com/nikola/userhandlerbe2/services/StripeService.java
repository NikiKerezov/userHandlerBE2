package com.nikola.userhandlerbe2.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikola.userhandlerbe2.utils.CustomerDetailsExtractor;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionCancelParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {
    private final CustomerDetailsExtractor customerDetailsExtractor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    public String cancelSubscription(String username) throws StripeException {
        username = username.replace("=", "");
        String apiKey = "sk_test_51Oh8D3GtZ4KJVRzkK5ApFFmMTNjjFX2T2INrxkuQDDgsMrzqh9vYdFj29xeKLfSHGG85DrpebDy7TRpXaAiCOgbv00IelceL5S";
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
                System.out.println(payload);
                System.out.println("Checkout session completed");
                System.out.println("Customer name: " + customerDetailsExtractor.extractCustomerName(payload));
                System.out.println("Customer email: " + customerDetailsExtractor.extractCustomerEmail(payload));
                System.out.println("Payment status: " + customerDetailsExtractor.extractPaymentStatus(payload));
                System.out.println("Subscription ID: " + customerDetailsExtractor.extractSubscriptionId(payload));
                userService.setEnabled(customerDetailsExtractor.extractCustomerEmail(payload), true);
                userService.setSubscriptionId(customerDetailsExtractor.extractCustomerEmail(payload), customerDetailsExtractor.extractSubscriptionId(payload));
                break;
            case "customer.subscription.deleted":
                System.out.println("Subscription deleted");
                System.out.println("Customer name: " + customerDetailsExtractor.extractCustomerName(payload));
                System.out.println("Customer email: " + customerDetailsExtractor.extractCustomerEmail(payload));
                System.out.println("Payment status: " + customerDetailsExtractor.extractPaymentStatus(payload));
                userService.setEnabled(customerDetailsExtractor.extractCustomerEmail(payload), false);
                break;
            case "invoice.payment_failed":
                System.out.println("Payment failed");
                System.out.println("Customer name: " + customerDetailsExtractor.extractCustomerName(payload));
                System.out.println("Customer email: " + customerDetailsExtractor.extractCustomerEmail(payload));
                System.out.println("Payment status: " + customerDetailsExtractor.extractPaymentStatus(payload));
                userService.setEnabled(customerDetailsExtractor.extractCustomerEmail(payload), false);
                break;
        }
    }
}
