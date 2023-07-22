package com.advertise.request.ad;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AgeGroupRequestDto {
    @NotNull(message = "value cannot be null")
    private String value;
}
