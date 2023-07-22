package com.advertise.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegionRequestDto {
    @NotNull(message = "Name cannot be null")
    private String name;
    private String Code;
}
