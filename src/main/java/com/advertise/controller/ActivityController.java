package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.Activity;
import com.advertise.request.ad.ActivityRequestDto;
import com.advertise.response.ErrorObject;
import com.advertise.response.ad.ActivityResponseDto;
import com.advertise.service.advertisement.ActivityService;
import com.jayway.jsonpath.internal.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/activity")
@SecurityRequirement(name = "Bearer Authentication")
public class ActivityController {

    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @Operation(summary = "Create a Activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Name will be unique", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addActivity")
    public ResponseEntity addActivity(@RequestBody ActivityRequestDto activityRequestDto) throws AdvartiseException {
        ActivityResponseDto responseDto= new ActivityResponseDto();
        if(Utils.isEmpty(activityRequestDto.getName())) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Please provide Name"), HttpStatus.BAD_REQUEST);
        }

        try {
            Activity activity = new Activity();
            activity.setName(activityRequestDto.getName());
            activity= service.save(activity);

            responseDto.setId(activity.getId());
            responseDto.setName(activity.getName());
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator");
        }

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Get All API
    @Operation(summary = "Get All Activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get All Activity", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllActivity")
    public Page<Activity> getAllActivity(@ParameterObject Pageable pageable) {
        return service.getAll(pageable);
    }

    @Operation(summary = "Update a Activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No Activity exists with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "302", description = "Activity name will be unique", content = @Content) })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateActivity/{id}")
    public ResponseEntity<Object> updateActivity(@Parameter(description = "id of Activity to be updated") @PathVariable("id") long id,
                                                @RequestBody ActivityRequestDto activityRequestDto) throws AdvartiseException {

        Activity activity;
        if(Utils.isEmpty(activityRequestDto.getName()))
            return new ResponseEntity<Object>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Activity name cannot be null/empty."), HttpStatus.BAD_REQUEST);

        Activity existingObject = service.findById(id);

        if (existingObject == null){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                    "Not Found,Please provide valid activityId"), HttpStatus.NOT_FOUND);
        }

        try {
            existingObject.setName(activityRequestDto.getName());
            activity = service.update(existingObject);
        }catch (Exception ex){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(activity, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteActivityById/{id}")
    public ResponseEntity<Object> deleteActivityById(@Parameter(description = "id of Activity to be deleted") @PathVariable("id") long id)
            throws AdvartiseException{
        Activity activity = service.findById(id);

        if(activity == null)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND
                    ,"Not found, Please provide valid activityId"), HttpStatus.NOT_FOUND);
        try {
            service.delete(activity);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
