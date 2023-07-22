package com.advertise.controller;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.Activity;
import com.advertise.entity.advertisement.Advertising;
import com.advertise.entity.advertisement.AgeGroup;
import com.advertise.entity.advertisement.Civility;
import com.advertise.entity.lookup.Country;
import com.advertise.entity.lookup.Region;
import com.advertise.entity.user.UserData;
import com.advertise.entity.user.UserDetails;
import com.advertise.request.ad.AdvertisingRequestDto;
import com.advertise.request.ad.TargetingRequestDto;
import com.advertise.response.ErrorObject;
import com.advertise.response.ad.AdvertisingResponseDto;
import com.advertise.security.SecurityContextUtils;
import com.advertise.service.UserService;
import com.advertise.service.advertisement.ActivityService;
import com.advertise.service.advertisement.AdvertisingService;
import com.advertise.service.advertisement.AgeGroupService;
import com.advertise.service.advertisement.CivilityService;
import com.advertise.service.lookup.LookupService;
import com.advertise.util.Statuses;
import com.advertise.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@RestController
@RequestMapping("/api/v1/ad")
@SecurityRequirement(name = "Bearer Authentication")
public class AdvertisingController {

    private final String VIDEO_FORMAT = "video/mp4";
    private static final long IMAGE_MAX_FILE_SIZE = 30 * 1024 * 1024;
    private static final long VEDIO_MAX_FILE_SIZE = 500 * 1024 * 1024;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LookupService lookupService;

    @Autowired
    private AgeGroupService ageGroupService;

    @Autowired
    private ActivityService activityService;
    @Autowired
    private AdvertisingService service;

    @Autowired
    private UserService userService;

    @Autowired
    private CivilityService civilityService;

    @Autowired
    public AdvertisingController(AdvertisingService advertisingService,ObjectMapper objectMapper
            ,LookupService lookupService,AgeGroupService ageGroupService,ActivityService activityService,UserService userService) {
        this.service = advertisingService;
        this.objectMapper = objectMapper;
        this.lookupService = lookupService;
        this.ageGroupService = ageGroupService;
        this.activityService = activityService;
        this.userService = userService;
    }


    // Get All API
    @Operation(summary = "Get All Advertisement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get All Advertisement", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Advertising.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getAllAdvertisement")
    public Page<AdvertisingResponseDto> getAllAdvertisements(@ParameterObject Pageable pageable) throws Exception {
        
        Page<Advertising> page = null;
        String userId =  SecurityContextUtils.getUserName();
        if(userId.equalsIgnoreCase("anonymous")){
            return new PageImpl<>(new ArrayList<>());
        }
        UserDetails currentUser= userService.findByUsername(userId);
        if(currentUser == null){
            return new PageImpl<>(new ArrayList<>());
        }
        UserData userData = currentUser.getUserData();
        TargetingRequestDto  userTargetingDto = null;
        if(userData != null && userData.getTargeting() != null){
            String userTargeting = userData.getTargeting();
            userTargetingDto = objectMapper.readValue(userTargeting, TargetingRequestDto.class);
        }
        List<String> userActivities =  new ArrayList<>();
        List<String> userAgeGroups = new ArrayList<>();
        List<String> userCountries = new ArrayList<>();
        List<String> userRegions = new ArrayList<>();
        List<String> userGenders = new ArrayList<>();
        if(userTargetingDto != null){
            userActivities.addAll(userTargetingDto.getActivity());
            userAgeGroups.addAll(userTargetingDto.getAgeGroup());
            userCountries.addAll(userTargetingDto.getCountry());
            userRegions.addAll(userTargetingDto.getRegion());
            userGenders.addAll(userTargetingDto.getGender());
        }


       //  Page<Advertising> entities = service.getAll(pageable);
        List<Advertising> advertisingList = service.findAll();
        List<Advertising> dataList = new ArrayList<>();
        for(Advertising add : advertisingList){
            String companyTargeting = add.getTargeting();
            if(companyTargeting != null){
                TargetingRequestDto  companyTargetingDto = objectMapper.readValue(companyTargeting, TargetingRequestDto.class);
                List<String> companyActivities =  companyTargetingDto.getActivity();
                List<String> companyAgeGroups = companyTargetingDto.getAgeGroup();
                List<String> companyCountries = companyTargetingDto.getCountry();
                List<String> companyRegions = companyTargetingDto.getRegion();
                List<String> companyGenders = companyTargetingDto.getGender();

                boolean noActivityInCommon = Collections.disjoint(companyActivities, userActivities);
                boolean noAgesInCommon = Collections.disjoint(companyAgeGroups, userAgeGroups);
                boolean noCountryInCommon = Collections.disjoint(companyCountries, userCountries);
                boolean noGenderInCommon = Collections.disjoint(companyGenders, userGenders);
                boolean noRegionInCommon = Collections.disjoint(companyRegions, userRegions);

                if(companyActivities.isEmpty() && userActivities.isEmpty()){
                    noActivityInCommon = false;
                }
                if(companyAgeGroups.isEmpty() && userAgeGroups.isEmpty()){
                    noAgesInCommon = false;
                }
                if(companyCountries.isEmpty() && userCountries.isEmpty()){
                    noCountryInCommon = false;
                }
                if(companyGenders.isEmpty() && userGenders.isEmpty()){
                    noGenderInCommon = false;
                }
                if(companyRegions.isEmpty() && userRegions.isEmpty()){
                    noRegionInCommon = false;
                }

                if(!noActivityInCommon && !noAgesInCommon && !noCountryInCommon
                        && !noRegionInCommon && !noGenderInCommon){
                    // System.out.println("==================");
                    dataList.add(add);
                }
            }
        }

        page = new PageImpl<>(dataList);
        return toPageObjectDto(page);
    }



    public Page<AdvertisingResponseDto> toPageObjectDto(Page<Advertising> objects) {
        Page<AdvertisingResponseDto> dtos = objects.map(this::convertToObjectDto);
        return dtos;
    }

    public AdvertisingResponseDto convertToObjectDto(Advertising add) {
        AdvertisingResponseDto dto = new AdvertisingResponseDto();
        //conversion here
        if (add != null) {
            dto = Util.convertClass(add, AdvertisingResponseDto.class);
            if (dto.getStartDate() != null) {
                dto.setStartDate(Util.isoDateTimeToStringDateTime(dto.getStartDate()));
            }
            if (dto.getEndDate() != null) {
                dto.setEndDate(Util.isoDateTimeToStringDateTime(dto.getEndDate()));
            }
        }
        return dto;
    }

    // Get API
    @Operation(summary = "Get Advertisement by reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Advertisement details", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Advertising.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/getAdvertisement/{reference}")
    public ResponseEntity getAdvertisementById(
            @Parameter(description = "reference of advertisement to be searched") @PathVariable("reference") String reference
            ){
        AdvertisingResponseDto responseDto = new AdvertisingResponseDto();
        try {

            Advertising add = service.findByReference(reference);

            if (add == null)
                return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND
                        , "Not found, Please provide valid reference"), HttpStatus.NOT_FOUND);

            responseDto = convertToObjectDto(add);

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator");
        }
        return Objects.nonNull(responseDto) ? new ResponseEntity<>(responseDto, HttpStatus.OK)
                : new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                "Not found,Please provide valid reference"), HttpStatus.NOT_FOUND);
    }

    // Create API
    @Operation(summary = "Create a Advertisement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Advertisement created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Advertising.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PostMapping(path = "/saveAdvertisement", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity saveAdd(
            @Parameter(description = "reference type is String")
            @RequestParam(value="reference",required = true)  String reference,
            @Parameter(description = "startDate type is LocalDateTime dd/MM/yyyy'T'HH:mm:ss")
            @RequestParam("startDate") @NotEmpty(message = "Start date cannot be blank")  @DateTimeFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "endDate type is LocalDateTime dd/MM/yyyy'T'HH:mm:ss")
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss") LocalDateTime endDate,
            @Parameter(description = "budget type is BigDecimal")
            @RequestParam("budget") BigDecimal budget,
            @Parameter(description = "rate type is BigDecimal")
            @RequestParam("rate") @NotNull BigDecimal rate,
            @Parameter(description = "isHeaderLocation type is Boolean")
            @RequestParam("isHeaderLocation") Boolean isHeaderLocation,
            @Parameter(description = "targeting type is JSON({\"country\":[\"France\"],\"region\":[\"Alsace\"],\"ageGroup\":[\"13-17 ans\"],\"activity\":[\"Alimentation\"],\"gender\":[\"Men\"]})")
            @RequestParam("targeting")@NotNull String targeting,
            @Parameter(description = "Primary file is File")
            @RequestParam("primaryFile") @NotNull MultipartFile primaryFile,
            @Parameter(description = "Secondary file  is File")
            @RequestParam("secondaryFile") @NotNull MultipartFile secondaryFile,
            @Parameter(description = "Status type is String(IN PROGRESS,PAUSE,DONE,FINISHED,COMPLETED,REMOVED)")
            @RequestParam("status") @NotNull String status,
            @Parameter(description = "Product Link is String")
            @RequestParam("productLink") @NotNull String productLink,
            @Parameter(description = "Title of Advertisement is String")
            @RequestParam("advertisementTitle")@NotNull String advertisementTitle,
            @Parameter(description = "Name of company is String")
            @RequestParam("companyName") @NotNull String companyName,
            @Parameter(description = "Description of company is String")
            @RequestParam("companyDescription") @NotNull String companyDescription,
            @Parameter(description = "Sample post title is String")
            @RequestParam("samplePostTitle") String samplePostTitle,
            @Parameter(description = "Sample post description is String")
            @RequestParam("samplePostDescription") String samplePostDescription,
            @Parameter(description = "Sample post file is file")
            @RequestParam("samplePostFile") MultipartFile samplePostFile) {

        if (reference.isEmpty()) {
            throw new IllegalStateException("Reference can't be empty");
        }
        if (startDate == null) {
            throw new IllegalStateException("Start date can't be empty");
        }
        if (rate == null) {
            throw new IllegalStateException("Rate can't be empty");
        }
        if (targeting.isEmpty()) {
            throw new IllegalStateException("Targeting can't be empty");
        }
        if (status.isEmpty()) {
            throw new IllegalStateException("Status can't be empty");
        }
        if (productLink.isEmpty()) {
            throw new IllegalStateException("Product link can't be empty");
        }
        if (advertisementTitle.isEmpty()) {
            throw new IllegalStateException("Advertisement Title can't be empty");
        }
        if (companyName.isEmpty()) {
            throw new IllegalStateException("Company Name can't be empty");
        }
        if (companyDescription.isEmpty()) {
            throw new IllegalStateException("Company Description can't be empty");
        }

        Advertising existingObj = service.findByReference(reference);
        if (existingObj != null && existingObj.getId() > 0) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT,
                    "Conflict,Advertising reference will be uniques"), HttpStatus.CONFLICT);
        }

        if (primaryFile.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty Primary File");
        }
        if (secondaryFile.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty Secondary File");
        }

        //Check if the file 1 is an image or video
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_JPEG.getMimeType(), VIDEO_FORMAT).contains(primaryFile.getContentType())) {
            throw new IllegalStateException("Primary File uploaded is not a valid format");
        }

        //Check if the file 2 is an image or video
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_JPEG.getMimeType(), VIDEO_FORMAT).contains(secondaryFile.getContentType())) {
            throw new IllegalStateException("Secondary File uploaded is not a valid format");
        }

        //Check if the Primary file size
        if (Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(primaryFile.getContentType())) {
            if (primaryFile.getSize() > IMAGE_MAX_FILE_SIZE) {
                throw new IllegalStateException("Primary File uploaded is exceed the limit");
            }
        } else if (Arrays.asList(VIDEO_FORMAT).contains(primaryFile.getContentType())) {
            if (primaryFile.getSize() > VEDIO_MAX_FILE_SIZE) {
                throw new IllegalStateException("Primary File uploaded is exceed the limit");
            }
        }

        //Check if the Secondary file size
        if (Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(secondaryFile.getContentType())) {
            if (secondaryFile.getSize() > IMAGE_MAX_FILE_SIZE) {
                throw new IllegalStateException("Secondary File uploaded is exceed the limit");
            }
        } else if (Arrays.asList(VIDEO_FORMAT).contains(secondaryFile.getContentType())) {
            if (secondaryFile.getSize() > VEDIO_MAX_FILE_SIZE) {
                throw new IllegalStateException("Secondary File uploaded is exceed the limit");
            }
        }

        if(!samplePostFile.isEmpty()){
            //Check if the file 3 is an image or video
            if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                    IMAGE_JPEG.getMimeType(), VIDEO_FORMAT).contains(samplePostFile.getContentType())) {
                throw new IllegalStateException("Sample post File uploaded is not a valid format");
            }

            if (Arrays.asList(IMAGE_PNG.getMimeType(),
                    IMAGE_JPEG.getMimeType()).contains(samplePostFile.getContentType())) {
                if (samplePostFile.getSize() > IMAGE_MAX_FILE_SIZE) {
                    throw new IllegalStateException("Sample post File uploaded is exceed the limit");
                }
            } else if (Arrays.asList(VIDEO_FORMAT).contains(samplePostFile.getContentType())) {
                if (primaryFile.getSize() > VEDIO_MAX_FILE_SIZE) {
                    throw new IllegalStateException("Sample post File uploaded is exceed the limit");
                }
            }
        }

        if(Statuses.findByValue(status) == null){
            throw new IllegalStateException("Status Not found, Please provide a valid status");
        }

        String jsonString = null;
        try {
            TargetingRequestDto  targetingRequestDto = objectMapper.readValue(targeting, TargetingRequestDto.class);
            TargetingRequestDto targetingDto = getTargetingDto(targetingRequestDto);

            ObjectMapper obj = new ObjectMapper();
            // Converting the Java object into a JSON string
            jsonString = obj.writeValueAsString(targetingDto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON can't be processed");
        }

        Advertising bean = new Advertising();
        bean.setReference(reference);
        bean.setStartDate(startDate);
        bean.setEndDate(endDate);
        bean.setBudget(budget);
        bean.setRate(rate);
        bean.setIsHeaderLocation(isHeaderLocation);
        bean.setTargeting(jsonString);
        bean.setPrimaryFile(primaryFile);
        bean.setSecondaryFile(secondaryFile);
        bean.setStatus(Statuses.findByValue(status).getValue());
        bean.setLinkUrlEnterpriseProduct(productLink);
        bean.setTitle(advertisementTitle);
        bean.setCompanyName(companyName);
        bean.setCompanyDescription(companyDescription);
        if(samplePostTitle != null){
            bean.setSampleTitle(samplePostTitle);
        }
        if(samplePostDescription != null){
          bean.setSampleDescription(samplePostDescription);
        }
        if(!samplePostFile.isEmpty()){
            bean.setSamplePostFile(samplePostFile);
        }

        Advertising advertisingSaved = service.save(bean);
        AdvertisingResponseDto dto = convertToObjectDto(advertisingSaved);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
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

    // Update API
    @Operation(summary = "Update a Advertising")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advertising updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AdvertisingResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No Advertisement exists with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "302", description = "Reference will be unique", content = @Content)})

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/updateAdvertisement/{reference}")
    public ResponseEntity<Object> updateAdvertisement(@Parameter(description = "reference of Advertisement to be updated") @PathVariable("reference") String reference,
                                                      @RequestBody AdvertisingRequestDto advertisingRequestDto) throws AdvartiseException {
        Advertising advertisingUpdated = null;
        Advertising advertisingExistingObject = service.findByReference(reference);

        if (advertisingExistingObject == null) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                    "Not Found,Please provide valid Advertisement Id"), HttpStatus.NOT_FOUND);
        }

        if(Statuses.findByValue(advertisingRequestDto.getStatus()).getValue() == null){
            throw new IllegalStateException("Status Not found, Please provide a valid status");
        }

        /*if (advertisingByReference != null && advertisingByReference.getId() != advertisingExistingObject.getId()) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT, "reference will be unique"),
                    HttpStatus.CONFLICT);
        }*/
        /*if (!advertisingRequestDto.getReference().equals(advertisingRequestDto.getReference())) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_CONFLICT, "reference will be same"),
                    HttpStatus.CONFLICT);
        }*/

        String jsonString = null;
        try {
            TargetingRequestDto  targetingRequestDto = advertisingRequestDto.getTargeting();
            TargetingRequestDto targetingDto = getTargetingDto(targetingRequestDto);

            ObjectMapper obj = new ObjectMapper();
            // Converting the Java object into a JSON string
            jsonString = obj.writeValueAsString(targetingDto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON can't be processed");
        }

        try {
          //  advertisingExistingObject.setReference(advertisingRequestDto.getReference());
            advertisingExistingObject.setEndDate(advertisingRequestDto.getEndDate());
            advertisingExistingObject.setBudget(advertisingRequestDto.getBudget());
            advertisingExistingObject.setDescriptionAdd(advertisingRequestDto.getDescriptionAdd());
            advertisingExistingObject.setStatus(Statuses.findByValue(advertisingRequestDto.getStatus()).getValue());
            advertisingExistingObject.setTargeting(jsonString);
            advertisingExistingObject.setLinkUrlEnterpriseProduct(advertisingRequestDto.getLinkUrlEnterpriseProduct());
            advertisingExistingObject.setTitle(advertisingRequestDto.getTitle());
            advertisingExistingObject.setCompanyDescription(advertisingRequestDto.getCompanyDescription());
            advertisingExistingObject.setSampleTitle(advertisingRequestDto.getSampleTitle());
            advertisingExistingObject.setSampleDescription(advertisingRequestDto.getSampleDescription());
            advertisingUpdated = service.update(advertisingExistingObject);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        AdvertisingResponseDto dto = convertToObjectDto(advertisingUpdated);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Advertisement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Advertisement deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @DeleteMapping("/deleteAdvertisement/{reference}")
    public ResponseEntity<Object> deleteAddByReference(@Parameter(description = "reference of Advertisement to be deleted") @PathVariable("reference") String reference)
            throws AdvartiseException {
        Advertising advertising = service.findByReference(reference);

        if (advertising == null) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND
                    , "Not found, Please provide valid reference"), HttpStatus.NOT_FOUND);
        }
        try {
            service.delete(advertising);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Update Advertisement Status only  API
    @Operation(summary = "Update a Advertising Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advertising status updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AdvertisingResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No Advertisement exists with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)})

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/changeStatus/{reference}/{status}")
    public ResponseEntity<Object> changeAdvertisementStatus(@Parameter(description = "reference of Advertisement to be updated") @PathVariable("reference") String reference,
                                                            @Parameter(description = "Status type is String(IN PROGRESS,PAUSE,DONE,FINISHED,COMPLETED,REMOVED)")
                                                            @PathVariable("status") String status) throws AdvartiseException {
        Advertising advertisingUpdated = null;


        Advertising advertisingExistingObject = service.findByReference(reference);

        if (advertisingExistingObject == null) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_NOT_FOUND,
                    "Not Found,Please provide valid Advertisement reference"), HttpStatus.NOT_FOUND);
        }
        if(Statuses.findByValue(status) == null){
            throw new IllegalStateException("Status Not found, Please provide a valid status");
        }

        try {
            advertisingExistingObject.setStatus(Statuses.findByValue(status).getValue());
            advertisingUpdated = service.update(advertisingExistingObject);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ErrorObject(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Some things went wrong,Please contact your administrator"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        AdvertisingResponseDto dto = convertToObjectDto(advertisingUpdated);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
