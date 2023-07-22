package com.advertise.request.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PasswordUpdateRequestDto {
    @NotNull(message = "username cannot be null")
    private String username;
    @NotNull(message = "password cannot be null")
    private String newPassword;
}
