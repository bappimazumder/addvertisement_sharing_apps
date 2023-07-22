package com.advertise.entity.user;

import com.advertise.entity.common.EntityCommon;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "USER_DETAILS")
public class UserDetails extends EntityCommon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username; // keycloak username

    private String userRefId; // keycloak id

 //   private String homeAddress;

    private LocalDate dateOfBirth;

    private String postalCode;

   private Long countryId;

   private Long regionId;

    private Boolean enabled;

    @OneToOne(mappedBy = "userDetails", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, optional = false)
    private UserData userData;
}
