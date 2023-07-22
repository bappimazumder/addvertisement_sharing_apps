package com.advertise.request.user;

import lombok.Data;

@Data
public class DeleteUserRequestDto {
    private String username;
    private String email;
}
