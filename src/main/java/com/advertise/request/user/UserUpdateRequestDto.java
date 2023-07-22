package com.advertise.request.user;

import com.advertise.request.ad.TargetingRequestDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserUpdateRequestDto {
  // @NotNull(message = "username cannot be null")
   // private String username;
//    @NotNull(message = "password cannot be null")
//    private String password;
    @NotNull(message = "Home address cannot be null")
    private String homeAddress;
    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;
    @NotNull(message = "Targeting cannot be null")
    private TargetingRequestDto targeting;

    private String firstName;

    private String lastName;

    private String civility;
}
