package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.AgeGroup;
import com.advertise.request.ad.AgeGroupRequestDto;
import com.advertise.response.ErrorObject;
import com.advertise.response.ad.AgeGroupResponseDto;
import com.advertise.service.advertisement.AgeGroupService;
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
@RequestMapping("/api/v1/ageGroup")
@SecurityRequirement(name = "Bearer Authentication")
public class AgeGroupController {

    private final AgeGroupService service;

    public AgeGroupController(AgeGroupService service) {
        this.service = service;
    }

    @Operation(summary = "Create a AgeGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AgeGroup created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AgeGroup.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Value will be unique", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addAgeGroup")
    public ResponseEntity addAgeGroup(@RequestBody AgeGroupRequestDto ageGroupRequestDto) throws AdvartiseException {
        AgeGroupResponseDto responseDto= new AgeGroupResponseDto();
        if(Utils.isEmpty(ageGroupRequestDto.getValue())) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Please provide Value"), HttpStatus.BAD_REQUEST);
        }

        try {
            AgeGroup ageGroup = new AgeGroup();
            ageGroup.setValue(ageGroupRequestDto.getValue());
            ageGroup= service.save(ageGroup);

            responseDto.setId(ageGroup.getId());
            responseDto.setValue(ageGroup.getValue());
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator");
        }

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Get All API
    @Operation(summary = "Get All AgeGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get All AgeGroup", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AgeGroup.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllAgeGroup")
    public Page<AgeGroup> getAllAgeGroup(@ParameterObject Pageable pageable) {
        return service.getAll(pageable);
    }

    @Operation(summary = "Update a AgeGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AgeGroup updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AgeGroupResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No Activity exists with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "302", description = "AgeGroup value will be unique", content = @Content) })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateAgeGroup/{id}")
    public ResponseEntity<Object> updateAgeGroup(@Parameter(description = "id of AgeGroup to be updated") @PathVariable("id") long id,
                                                @RequestBody AgeGroupRequestDto ageGroupRequestDto) throws AdvartiseException {

        AgeGroup activity;
        if(Utils.isEmpty(ageGroupRequestDto.getValue()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Age group value cannot be null/empty."), HttpStatus.BAD_REQUEST);

        AgeGroup existingObject = service.findById(id);

        if (existingObject == null){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                    "Not Found,Please provide valid ageGroupId"), HttpStatus.NOT_FOUND);
        }

        try {
            existingObject.setValue(ageGroupRequestDto.getValue());
            activity = service.update(existingObject);
        }catch (Exception ex){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(activity, HttpStatus.OK);
    }

    @Operation(summary = "Delete a AgeGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "AgeGroup deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteAgeGroupById/{id}")
    public ResponseEntity<Object> deleteAgeGroupById(@Parameter(description = "id of AgeGroup to be deleted") @PathVariable("id") long id)
            throws AdvartiseException{
        AgeGroup object = service.findById(id);

        if(object == null)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND
                    ,"Not found, Please provide valid ageGroupId"), HttpStatus.NOT_FOUND);
        try {
            service.delete(object);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
