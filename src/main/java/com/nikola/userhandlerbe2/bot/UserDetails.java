package com.nikola.userhandlerbe2.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    Long id;
    //TODO IMPLEMENT JWT TOKEN CHECK FOR USER SO HE CAN OR CANNOT TALK WITH THE BOT
    String jwtToken;
}
