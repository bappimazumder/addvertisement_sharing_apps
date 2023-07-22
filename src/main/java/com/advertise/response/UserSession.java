package com.advertise.response;

import lombok.Data;

@Data
public class UserSession {
    private String token;

    private String username;

    private String refreshToken;

    private String userId;
}
