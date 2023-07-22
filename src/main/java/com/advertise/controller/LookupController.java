package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.lookup.Region;
import com.advertise.entity.lookup.Country;
import com.advertise.repository.RegionRepository;
import com.advertise.repository.CountryRepository;
import com.advertise.request.RegionRequestDto;
import com.advertise.request.CountryRequestDto;
import com.advertise.response.ErrorObject;
import com.advertise.response.lookup.CountryResponseDto;
import com.advertise.service.lookup.LookupService;
import com.advertise.serviceImpl.UserServiceImpl;
import com.advertise.util.Util;
import com.jayway.jsonpath.internal.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/lookup")
@Log4j2
public class LookupController {
    private final LookupService lookupService;
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;

    private final UserServiceImpl userServiceImpl;
    @Autowired
    public LookupController(LookupService lookupService, CountryRepository countryRepository
            ,RegionRepository regionRepository,UserServiceImpl userServiceImpl){
        this.lookupService=lookupService;
        this.countryRepository=countryRepository;
        this.regionRepository = regionRepository;
        this.userServiceImpl =userServiceImpl;
    }

    @Operation(summary = "Create a country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "country created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Name will be unique", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addCountry")
    public ResponseEntity addCountry(@RequestBody CountryRequestDto countryRequestDto) throws AdvartiseException {
        CountryResponseDto responseDto=null;
        if(Utils.isEmpty(countryRequestDto.getName()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Please provide Name"), HttpStatus.BAD_REQUEST);

        Country country = lookupService.getCountryByName(countryRequestDto.getName());
        if(country !=null)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT,
                    "Conflict,Country name will be uniques"), HttpStatus.CONFLICT);

        try {
            responseDto= lookupService.saveCountry(countryRequestDto);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator");
        }

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a country by country id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "found the country", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })

    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/getCountryById/{id}")
    public ResponseEntity getCountryById(@Parameter(description = "id of country to be searched") @PathVariable("id") Long id) {
        Country country=null;
        try{
            CountryResponseDto countryResponseDto = lookupService.getCountryById(id);
            if(countryResponseDto !=null)
                country= Util.convertClass(countryResponseDto,Country.class);

        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator");
        }

        return country !=null ? new ResponseEntity<>(country, HttpStatus.OK)
                : new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                "Not found,Please provide valid countryId"),HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "This is to fetch all the countries stored in db ")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "fetched all the countries from db",content = {
                            @Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            })
    @GetMapping("/getAllCountry")
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Page<Country> getAllCountry(@ParameterObject Pageable pageable) {
        return lookupService.getAllCountry(pageable);
    }

    @Operation(summary = "Update a Country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Country updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No Country exists with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "302", description = "Country name will be unique", content = @Content) })
    @PutMapping("/updateCountry/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateCountry(@Parameter(description = "id of country to be updated") @PathVariable("id") long id,
                                                 @RequestBody CountryRequestDto countryRequestDto) throws Exception {
        Country country=null;
        Country updatedCountry=null;
        CountryResponseDto saveCountry;

        if(Utils.isEmpty(countryRequestDto.getName()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST,
                    "Country name cannot be null/empty."), HttpStatus.BAD_REQUEST);

        CountryResponseDto responseDto= lookupService.getCountryById(id);

        if (responseDto==null)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                    "Not Found,Please provide valid countryId"), HttpStatus.NOT_FOUND);

        if(responseDto !=null)
            country=Util.convertClass(responseDto,Country.class);
        Country countryEntity =lookupService.getCountryByName(countryRequestDto.getName());

        if(countryEntity !=null && countryEntity.getId() !=id)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT,"Name will be unique"),
                    HttpStatus.CONFLICT);

        try {
            country.setName(countryRequestDto.getName());
            country.setCountryCode(countryRequestDto.getCountryCode());
             saveCountry = lookupService.updateCountry(id,countryRequestDto);
            if(saveCountry !=null)
                updatedCountry=Util.convertClass(saveCountry,Country.class);
        }catch (Exception ex){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(updatedCountry, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Country deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    @DeleteMapping("/deleteCountryById/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteCountryById(@Parameter(description = "id of Country to be deleted") @PathVariable("id") long id)
            throws Exception {
        CountryResponseDto country =lookupService.getCountryById(id);

        if(country==null)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND
                    ,"Not found, Please provide valid countryId"), HttpStatus.NOT_FOUND);

        try {
            lookupService.deleteCountryById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Create a Region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Region created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Region.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content) })
    @PostMapping("/addRegion")
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addRegion(@RequestBody RegionRequestDto reqDto) throws AdvartiseException {
        Region savedRegion;
        if(Utils.isEmpty(reqDto.getName()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST
                    ,"Please provide Region name"),HttpStatus.BAD_REQUEST);

        Region region = Util.convertClass(reqDto, Region.class);

        try {
            savedRegion = lookupService.saveRegion(region);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
            ,"Some things went wrong,Please contact your administrator"),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(savedRegion,HttpStatus.CREATED);

    }

    @Operation(summary = "Get a Region by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "found the Region", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })

    @GetMapping("/getRegionById/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity getRegionById(@Parameter(description = "id of Region to be searched") @PathVariable("id") Long id) throws Exception {
        Region region =lookupService.getRegionById(id);
        return region!=null ? new ResponseEntity<>(region, HttpStatus.OK) :
                new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                        "Not found, Please provide valid regionId"),HttpStatus.NOT_FOUND);
    }
    @Operation(summary = "This is to fetch all the cities stored in db ")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "fetched all the cities from db",content = {
                            @Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            })
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/getAllRegion")
    public Page<Region> getAllRegion(@ParameterObject Pageable pageable) {
        return lookupService.getAllRegion(pageable);
    }

    @Operation(summary = "Update a Region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Region updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No Region exists with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("/updateRegion/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateRegion(@Parameter(description = "id of Region to be updated") @PathVariable("id") long id,
                                           @Valid @RequestBody RegionRequestDto regionRequestDto) throws Exception {
        Region region=null;
        Region updatedRegion;

        if(Utils.isEmpty(regionRequestDto.getName()))
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_BAD_REQUEST
                    ,"Region name cannot be null/empty."), HttpStatus.BAD_REQUEST);

         region = lookupService.getRegionById(id);

        if (region==null)
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND
                    ,"No Region exists with given id"), HttpStatus.NOT_FOUND);

        try{
            region.setName(regionRequestDto.getName());
            region.setCode(regionRequestDto.getCode());
            updatedRegion =lookupService.saveRegion(region);

        }catch (Exception e){
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(updatedRegion, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Region deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    @DeleteMapping("/deleteRegionById/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteRegionById(@Parameter(description = "id of Region to be deleted") @PathVariable("id") long id)
            throws AdvartiseException {
        try {

           Region regionOptional = lookupService.getRegionById(id);

            if(regionOptional==null)
                return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND
                        ,"Not found, Please provide valid regionId"), HttpStatus.NOT_FOUND);
            lookupService.deleteRegionById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
