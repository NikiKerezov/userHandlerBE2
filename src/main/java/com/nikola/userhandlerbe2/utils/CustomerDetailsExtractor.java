package com.nikola.userhandlerbe2.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@NoArgsConstructor
@Data
public class CustomerDetailsExtractor {
    ObjectMapper mapper = new ObjectMapper();
    String customerName;
    String customerEmail;
    String paymentStatus;

    private String grep(String regex, String payload) {
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(payload);
      if (matcher.find()) {
        return matcher.group(1);
      } else {
          throw new RuntimeException("No match found for the regex: " + regex);
      }
    }

    private void extractAll(String payload) {
        try {
            Map<String, Object> data = mapper.readValue(payload, Map.class);
            String customerDetails = data.get("data").toString();
            String paymentStatus = data.get("data").toString();
            try {
                customerName = grep("name=(.*?),", customerDetails);
                customerEmail = grep("email=(.*?),", customerDetails);
                this.paymentStatus = grep("payment_status=(.*?),", paymentStatus);
            } catch (RuntimeException e) {
                System.out.println("Error extracting customer details: " + e.getMessage());
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error deserializing the payload: " + e.getMessage());
        }

    }

    public String extractCustomerName(String payload) {
        if (customerName == null) {
            extractAll(payload);
        }
        return customerName;
    }

    public String extractCustomerEmail(String payload) {
        if (customerEmail == null) {
            extractAll(payload);
        }
        return customerEmail;
    }

    public String extractPaymentStatus(String payload) {
        if (paymentStatus == null) {
            extractAll(payload);
        }
        return paymentStatus;
    }
}
