package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.user.UserDetails;
import com.advertise.exception.AccountNotFoundException;
import com.advertise.exception.ConflictAccountException;
import com.advertise.exception.CreateAccountException;
import com.advertise.repository.CountryRepository;
import com.advertise.repository.RegionRepository;
import com.advertise.repository.UserDetailsRepository;
import com.advertise.request.user.CreateUserRequestDto;
import com.advertise.request.user.UserCredentials;
import com.advertise.response.AuthTokenInfo;
import com.advertise.response.ErrorObject;
import com.advertise.service.UserService;
import com.advertise.service.advertisement.CivilityService;
import com.jayway.jsonpath.internal.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.ws.rs.NotAuthorizedException;

@RestController
@RequestMapping("/api/user")
public class OauthController {
    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;

    @Autowired
    private CivilityService civilityService;


    @Autowired
    public OauthController(UserService userService,UserDetailsRepository userDetailsRepository
            ,CountryRepository countryRepository,RegionRepository regionRepository){
        this.userService=userService;
        this.userDetailsRepository=userDetailsRepository;
        this.countryRepository=countryRepository;
        this.regionRepository =regionRepository;
    }
    @Operation(summary = "Get user access details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get token details successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuthTokenInfo.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)})
    @PostMapping(value = "/token")
    public ResponseEntity<Object> getToken(@RequestBody UserCredentials userCredentials) throws AccountNotFoundException,AdvartiseException {
        AccessTokenResponse responseToken=null;

        if(Utils.isEmpty(userCredentials.getUsername()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Username cannot be null"), HttpStatus.BAD_REQUEST);

        if(Utils.isEmpty(userCredentials.getPassword()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Password cannot be null"), HttpStatus.BAD_REQUEST);

        try{
            responseToken= userService.getToken(userCredentials);
        } catch (NotAuthorizedException e) {
//            throw new AccountNotFoundException(e.getMessage(),HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
                    "Please provide valid username/password"), HttpStatus.UNAUTHORIZED);

        }catch (Exception e){
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);

        }

//         return new ResponseEntity<>(Util.convertObjToString(responseToken), HttpStatus.OK);
         return new ResponseEntity<>(responseToken, HttpStatus.OK);
    }

    @Operation(summary = "Create a user")
//    @ResponseStatus(CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetails.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content) })
    @PostMapping("/createUser")
    public ResponseEntity createUser(@Valid @RequestBody CreateUserRequestDto reqDto) throws AdvartiseException {

        if (reqDto.getUsername().isEmpty()) {
            throw new IllegalStateException("User name can't be empty");
        }
       if(reqDto.getCountryId() !=null && reqDto.getCountryId()>0){
            if(!countryRepository.findById(reqDto.getCountryId()).isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide valid countryId");
        }

        if(reqDto.getRegionId() !=null && reqDto.getRegionId()>0){
            if(!regionRepository.findById(reqDto.getRegionId()).isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide valid regionId");
        }

        if(reqDto.getCivility() != null){
            if(civilityService.findByValue(reqDto.getCivility()) == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide valid civility value");
        }

        try {
            UserDetails checkObject = userDetailsRepository.findByUsername(reqDto.getUsername());

            if(checkObject !=null)
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already exist");
                return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT,
                        "Account already exist,Please provide unique username"),HttpStatus.CONFLICT);

            userService.saveUser(reqDto);

        } catch (ConflictAccountException e) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT,
                    "Account already exist,Please provide unique username"),HttpStatus.CONFLICT);
        } catch (CreateAccountException e) {
//            throw new ResponseStatusException(
//                    HttpStatus.INTERNAL_SERVER_ERROR, "Cannot create account", e);
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }


}
