package com.advertise.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CountryRequestDto {
    @NotNull(message = "Name cannot be null")
    private String name;
    private String countryCode;
}
