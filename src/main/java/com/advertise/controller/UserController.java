package com.advertise.controller;
import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.Activity;
import com.advertise.entity.advertisement.AgeGroup;
import com.advertise.entity.advertisement.Civility;
import com.advertise.entity.lookup.Country;
import com.advertise.entity.lookup.Region;
import com.advertise.entity.user.UserData;
import com.advertise.entity.user.UserDetails;
import com.advertise.exception.AccountNotFoundException;
import com.advertise.repository.UserDataRepository;
import com.advertise.repository.UserDetailsRepository;
import com.advertise.request.ad.TargetingRequestDto;
import com.advertise.request.user.CreateUserRequestDto;
import com.advertise.request.user.DeleteUserRequestDto;
import com.advertise.request.user.PasswordUpdateRequestDto;
import com.advertise.request.user.UserUpdateRequestDto;
import com.advertise.response.ErrorObject;
import com.advertise.response.user.UserResponseDto;
import com.advertise.security.SecurityContextUtils;
import com.advertise.service.UserService;
import com.advertise.service.advertisement.ActivityService;
import com.advertise.service.advertisement.AgeGroupService;
import com.advertise.service.advertisement.CivilityService;
import com.advertise.service.lookup.LookupService;
import com.advertise.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

@RestController
@RequestMapping("/api/v1")
@Log4j2
public class UserController {
    UserService userService;
    @Autowired
    private LookupService lookupService;
    @Autowired
    private AgeGroupService ageGroupService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private CivilityService civilityService;
    UserDetailsRepository userDetailsRepository;

    @Autowired
    UserDataRepository userDataRepository;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService,UserDetailsRepository userDetailsRepository){
        this.userService =userService;
        this.userDetailsRepository =userDetailsRepository;

    }
    @Operation(summary = "Get User details by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "found the user details", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetails.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<Object> getByUsername(@PathVariable("username") String username) throws AccountNotFoundException {

        UserDetails userDetails = null;
        try {

            if(!username.equalsIgnoreCase(SecurityContextUtils.getUserName()))
                return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                        "Please provide valid username"), HttpStatus.BAD_REQUEST);

            userDetails =userService.findByUsername(username);

            if(userDetails ==null)
                return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                        "Please provide valid username"), HttpStatus.NOT_FOUND);

            if(!StringUtils.isEmpty(userDetails.getUsername()) && !userDetails.getUsername().equalsIgnoreCase(username))
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                    "Account not found"), HttpStatus.NOT_FOUND);

        }catch (ForbiddenException ex){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_FORBIDDEN,
                    "Forbidden"), HttpStatus.FORBIDDEN);
        }catch (Exception e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserResponseDto dto = convertToObjectDto(userDetails);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public UserResponseDto convertToObjectDto(UserDetails userDetails) {
        UserResponseDto dto = new UserResponseDto();
        //conversion here
        if (userDetails != null) {
            dto = Util.convertClass(userDetails, UserResponseDto.class);
            UserData userData = userDetails.getUserData();
            if(userData != null){
                dto.setPhoneNumber(userData.getPhoneNumber());
                dto.setHomeAddress(userData.getHomeAddress());
                dto.setTargeting(userData.getTargeting());
                dto.setSocialMediaLinks(userData.getSocialMediaLinks());
                if(userData.getCivility() != null){
                    dto.setCivility(userData.getCivility().getValue());
                }
            }
        }
        return dto;
    }

    @Operation(summary = "Update a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No user exists with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "302", description = "user name value will be unique", content = @Content) })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUserDetails/{id}")
    public ResponseEntity<Object> updateUserDetails(@Parameter(description = "id of user to be updated") @PathVariable("id") long id,
                                                     @RequestBody @Valid UserUpdateRequestDto requestDto) throws Exception {

        UserDetails userDetails;
       /*if(Utils.isEmpty(requestDto.getUsername()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                   "User Name cannot be null/empty."), HttpStatus.BAD_REQUEST);*/

        UserDetails existingObject = userService.findById(id);
        if (existingObject == null){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                    "Not Found,Please provide valid user id"), HttpStatus.NOT_FOUND);
        }
       /*  if(requestDto.getUsername() != null && !existingObject.getUsername().equals(requestDto.getUsername())){
          /* UserDetails existUserName = userService.findByUsername(requestDto.getUsername());
           if(existUserName != null){
               return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT,
                       "User already exist,Please provide valid user name"), HttpStatus.CONFLICT);
           }
        }*/
        try {
            if(requestDto.getFirstName() !=null)
                  existingObject.setFirstName(requestDto.getFirstName());
            if(requestDto.getLastName() !=null)
                  existingObject.setLastName(requestDto.getLastName());
           /* if(requestDto.getUsername() !=null)
                  existingObject.setUsername(requestDto.getUsername());*/

            if(existingObject.getUserData() != null){
                existingObject.getUserData().setPhoneNumber(requestDto.getPhoneNumber());
                existingObject.getUserData().setHomeAddress(requestDto.getHomeAddress());
                TargetingRequestDto targetingRequestDto = requestDto.getTargeting();
                

                if(targetingRequestDto != null){
                    String jsonStringTargeting = getStringTargeting(targetingRequestDto);
                    existingObject.getUserData().setTargeting(jsonStringTargeting);
                }
                if(requestDto.getCivility() != null){
                    Civility civility = civilityService.findByValue(requestDto.getCivility());
                    existingObject.getUserData().setCivility(civility);
                }
            }

            userDetails = userService.update(existingObject);
        }catch (Exception ex){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        UserResponseDto dto = convertToObjectDto(userDetails);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private TargetingRequestDto getTargetingDto(TargetingRequestDto targetingRequestDto) {
        TargetingRequestDto  targetingDto = new TargetingRequestDto();

        for(String name : targetingRequestDto.getCountry()){
            Country country = lookupService.getCountryByName(name);
            if(country != null){
                targetingDto.getCountry().add(country.getName());
            }
        }

        for(String name : targetingRequestDto.getRegion()){
            Region region = lookupService.getRegionByName(name);
            if(region != null){
                targetingDto.getRegion().add(region.getName());
            }
        }

        for(String name : targetingRequestDto.getAgeGroup()){
            AgeGroup ageGroup = ageGroupService.findByValue(name);
            if(ageGroup != null){
                targetingDto.getAgeGroup().add(ageGroup.getValue());
            }
        }

        for(String name : targetingRequestDto.getActivity()){
            Activity activity = activityService.findByName(name);
            if(activity != null){
                targetingDto.getActivity().add(activity.getName());
            }
        }

        for(String value : targetingRequestDto.getGender()){
            Civility civility = civilityService.findByValue(value);
            if(civility != null){
                targetingDto.getGender().add(civility.getValue());
            }
        }
        return targetingDto;
    }

    private String getStringTargeting(TargetingRequestDto targetingRequestDto) {
        String jsonStringTargeting;
        try {
            TargetingRequestDto targetingDto = getTargetingDto(targetingRequestDto);
            ObjectMapper obj = new ObjectMapper();
            jsonStringTargeting = obj.writeValueAsString(targetingDto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON can't be processed");
        }
        return jsonStringTargeting;
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Password updated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content) })
    @PutMapping("/updatePassword")
    public ResponseEntity passwordUpdate(@Valid @RequestBody PasswordUpdateRequestDto req) throws AccountNotFoundException, AdvartiseException {
        Boolean response=false;
        try {
            response = userService.passwordChange(req);
        }catch (NotAuthorizedException e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
                    "Please provide valid username"), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(!response)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
                    "Please provide valid username"), HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete user successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content) })
    @DeleteMapping("/deleteUser")
    public ResponseEntity deleteUser(@Valid @RequestBody DeleteUserRequestDto req) throws AccountNotFoundException, AdvartiseException {
        Boolean response=false;
        try {

            if(req.getEmail()==null && req.getUsername()==null)
                return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                        "Please provide valid username/email"), HttpStatus.BAD_REQUEST);

            response = userService.deleteUser(req);
        }catch (NotAuthorizedException e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
                    "Please provide valid username"), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(!response)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
                    "Please provide valid username/email"), HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
