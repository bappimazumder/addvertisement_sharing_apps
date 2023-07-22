package com.advertise.request.user;

import com.advertise.request.ad.TargetingRequestDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CreateUserRequestDto {
    @NotNull(message = "firstname cannot be null")
    private String firstName;
    @NotNull(message = "lastname cannot be null")
    private String lastName;
    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "username cannot be null")
    private String username;
    @NotNull(message = "password cannot be null")
    private String password;
    @NotNull(message = "Home address cannot be null")
    private String homeAddress;
    @NotNull(message = "Date of birth cannot be null")
    private LocalDate dateOfBirth;
    private String postalCode;
    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;
    private String socialMediaLinks;
    @NotNull(message = "Targeting cannot be null")
    private TargetingRequestDto targeting;
    @NotNull(message = "Civility cannot be null")
    private String civility;
    private Long countryId;
    private Long regionId;
}
