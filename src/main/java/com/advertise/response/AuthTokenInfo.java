package com.advertise.response;

import lombok.Data;

@Data
public class AuthTokenInfo {
    private String access_token;
    private String refresh_token;
    private String expires_in;
    private String refresh_expires_in;
    private String token_type;
    private String id_token;
    private String session_state;
    private String scope;
}
