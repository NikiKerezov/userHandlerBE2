package com.nikola.userhandlerbe2.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetTelegramIdRequest {
    String username;
    String telegramId;
}
