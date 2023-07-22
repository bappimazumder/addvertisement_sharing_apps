package com.advertise.request.user;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserCredentials {
    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Password cannot be null")
    private String password;
}
