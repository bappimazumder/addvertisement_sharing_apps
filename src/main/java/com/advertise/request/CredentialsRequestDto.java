package com.advertise.request;

import lombok.Data;

@Data
public class CredentialsRequestDto {
    private String type;
    private String value;
    private Boolean temporary;
}
