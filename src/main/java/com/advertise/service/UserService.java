package com.advertise.service;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.user.UserDetails;
import com.advertise.exception.AccountNotFoundException;
import com.advertise.exception.CreateAccountException;
import com.advertise.request.user.CreateUserRequestDto;
import com.advertise.request.user.DeleteUserRequestDto;
import com.advertise.request.user.PasswordUpdateRequestDto;
import com.advertise.request.user.UserCredentials;
import com.advertise.response.user.UserResponseDto;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.NotFoundException;

public interface UserService {
    AccessTokenResponse getToken(UserCredentials userCredentials) throws AdvartiseException;
    UserDetails saveUser(CreateUserRequestDto userDetails) throws AdvartiseException, CreateAccountException;
    UserRepresentation getUserByUsername(String userId) throws AccountNotFoundException;

    UserDetails findByUsername(String username) throws Exception;

    UserDetails findById(long id) throws Exception;
    UserDetails update(UserDetails userDetails);

    Boolean passwordChange(PasswordUpdateRequestDto requestDto);
    Boolean deleteUser(DeleteUserRequestDto requestDto);

}
