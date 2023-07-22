package com.advertise.response.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.time.LocalDate;

@Data
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserResponseDto {
    private String id;
    private String username;
    private String firstName;
    private Boolean enabled;
    private String lastName;
    private String email;
    private String homeAddress;
    private LocalDate dateOfBirth;
    private String socialMediaLinks;
    private String phoneNumber;
    private String targeting;
    private String civility;

   // private AccessDto access;


//    [
//    {
//        "id": "0d4dc483-17f2-4281-b29a-30e64ea7115b",
//            "createdTimestamp": 1665941756466,
//            "username": "user2",
//            "enabled": true,
//            "totp": false,
//            "emailVerified": false,
//            "firstName": "Shammi",
//            "lastName": "Akter",
//            "email": "shammi.monir@gmail.com",
//            "disableableCredentialTypes": [],
//        "requiredActions": [],
//        "notBefore": 0,
//            "access": {
//        "manageGroupMembership": true,
//                "view": true,
//                "mapRoles": true,
//                "impersonate": false,
//                "manage": true
//    }
//    }
//]

}
