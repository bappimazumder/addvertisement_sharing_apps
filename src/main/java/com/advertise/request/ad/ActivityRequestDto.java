package com.advertise.request.ad;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActivityRequestDto {
    @NotNull(message = "Name cannot be null")
    private String name;
}
