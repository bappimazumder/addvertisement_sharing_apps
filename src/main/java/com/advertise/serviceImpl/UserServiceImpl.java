package com.advertise.serviceImpl;

import com.advertise.config.AdvartiseException;
import com.advertise.entity.advertisement.Activity;
import com.advertise.entity.advertisement.AgeGroup;
import com.advertise.entity.advertisement.Civility;
import com.advertise.entity.lookup.Country;
import com.advertise.entity.lookup.Region;
import com.advertise.entity.user.UserData;
import com.advertise.entity.user.UserDetails;
import com.advertise.exception.AccountNotFoundException;
import com.advertise.exception.ConflictAccountException;
import com.advertise.exception.CreateAccountException;
import com.advertise.repository.UserDataRepository;
import com.advertise.repository.UserDetailsRepository;
import com.advertise.request.ad.TargetingRequestDto;
import com.advertise.request.user.CreateUserRequestDto;
import com.advertise.request.user.DeleteUserRequestDto;
import com.advertise.request.user.PasswordUpdateRequestDto;
import com.advertise.request.user.UserCredentials;
import com.advertise.security.SecurityContextUtils;
import com.advertise.service.UserService;
import com.advertise.service.advertisement.ActivityService;
import com.advertise.service.advertisement.AgeGroupService;
import com.advertise.service.advertisement.CivilityService;
import com.advertise.service.lookup.LookupService;
import com.advertise.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${keycloak.credentials.secret}")
    private String SECRETKEY;
    @Value("${keycloak.resource}")
    private String CLIENTID;
    @Value("${keycloak.auth-server-url}")
    private String AUTHURL;
    @Value("${keycloak.realm}")
    private String REALM;

    @Value("#{environment.KC_PROXY_HOST ?: ''}")
    private String KC_PROXY_HOST;

    @Value("#{environment.KC_PROXY_PORT ?: '3128'}")
    private String KC_PROXY_PORT;

    @Value("#{environment.KC_EXTERN_PROXY_HOST ?: ''}")
    private String KC_EXTERN_PROXY_HOST;

    @Value("#{environment.KC_EXTERN_PROXY_PORT ?: '3128'}")
    private String KC_EXTERN_PROXY_PORT;

    @Value("#{environment.KC_CONNEXION_POOL_SIZE ?: 20}")
    private int KC_CONNEXION_POOL_SIZE;

    @Value("#{environment.KC_CONNEXION_TTL ?: 10L}")
    private long KC_CONNEXION_TTL;

    @Value("#{environment.KC_IAM_CLI_CLIENT_ID ?: 'login-app'}")
    private String KC_IAM_CLI_CLIENT_ID;

    @Value("#{environment.KC_IAM_CLI_CLIENT_SECRET ?: '9KZHC4Fh7XVTUtYzEcRAQiGmUfzCAjv8'}")
    private String KC_IAM_CLI_CLIENT_SECRET;

    private final UserDetailsRepository userDetailsRepository;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private Keycloak instanceKeycloak;

    private static final String KEYCLOAK_ERROR_MSG = "Error calling keycloak API, HTTP error code: ";

    @Autowired
    private LookupService lookupService;

    @Autowired
    private AgeGroupService ageGroupService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private CivilityService civilityService;

    @Autowired
    public UserServiceImpl(UserDetailsRepository userDetailsRepository){
        this.userDetailsRepository=userDetailsRepository;
    }

    @Override
    public AccessTokenResponse getToken(UserCredentials userCredentials)  throws AdvartiseException {
        AccessTokenResponse response=null;
         response =getKeycloakInstanceForUserToken(userCredentials).tokenManager().getAccessToken();
        return response;
    }

    @Timed("Performance")
    private Keycloak getKeycloakInstanceForUserToken(UserCredentials userCredentials){
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("keycloakInstance with Parameters", AUTHURL, REALM, userCredentials.getUsername(), userCredentials.getPassword(), CLIENTID, SECRETKEY));
        }

        Keycloak instance = Keycloak.getInstance(AUTHURL, REALM, userCredentials.getUsername(), userCredentials.getPassword(), CLIENTID, SECRETKEY);
        return instance;

    }


    public String createUser(CreateUserRequestDto userDTO) {

        int statusId = 0;
        String userId = null;
        try {

            CredentialRepresentation passwordCred = new CredentialRepresentation();

            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userDTO.getPassword());
            passwordCred.setTemporary(false);

            UserRepresentation user = new UserRepresentation();

            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEnabled(true);

            user.setCredentials(Arrays.asList(passwordCred));

            UsersResource userRessource = keycloakInstanceForLoginUser().realm(REALM).users();
            Response result = userRessource.create(user);
            statusId = result.getStatus();

            if (statusId == 201) {
                userId = CreatedResponseUtil.getCreatedId(result);

                // set role
//                RealmResource realmResource = getRealmResource();
//                RoleRepresentation savedRoleRepresentation = realmResource.roles().get("user").toRepresentation();
//                realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(savedRoleRepresentation));
                log.debug(userDTO.getUsername()+" created successfully");
            }

            else if (statusId == 409) {
                log.debug(userDTO.getUsername()+" already available.");

            } else {
                log.debug(userDTO.getUsername()+" could not be created.");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return userId;
    }

    private void assignRole(String userId){
        Keycloak keycloakInstance=  getInstanceKeycloak(AUTHURL, KC_PROXY_HOST, KC_PROXY_PORT, KC_IAM_CLI_CLIENT_ID,
                KC_IAM_CLI_CLIENT_SECRET);

        // get the user by their ID
        UserRepresentation user = keycloakInstance.realm(REALM).users().get(userId).toRepresentation();

        // get the ID of the "user" role
        RoleRepresentation userRole = keycloakInstance.realm(REALM).roles().get("USER").toRepresentation();

        // assign the "user" role to the user
        keycloakInstance.realm(REALM).users().get(user.getId()).roles().realmLevel().add(Arrays.asList(userRole));

    }


    @Override
    public UserDetails saveUser(CreateUserRequestDto requestDto) throws AdvartiseException, CreateAccountException {
        UserDetails userDetailsResponse=null;

        //String userId=createUser(userDetails);
        String userId = createAccount(requestDto);

        if(userId !=null){
            assignRole(userId);
            UserDetails newObject= Util.convertClass(requestDto, UserDetails.class);
            newObject.setUserRefId(userId);
            newObject.setEnabled(true);

            String jsonStringTargeting = null;
            jsonStringTargeting = getStringTargeting(requestDto);

            UserData userData = new UserData();
            userData.setHomeAddress(requestDto.getHomeAddress());
            userData.setTargeting(jsonStringTargeting);
            userData.setPhoneNumber(requestDto.getPhoneNumber());
            userData.setSocialMediaLinks(requestDto.getSocialMediaLinks());
            if(requestDto.getCivility() != null){
                Civility civility = civilityService.findByValue(requestDto.getCivility());
                userData.setCivility(civility);
            }
            userData.setUserDetails(newObject);
            newObject.setUserData(userData);
            userDetailsResponse = Util.convertClass(userDetailsRepository.save(newObject),UserDetails.class);
        }

        return userDetailsResponse;
    }

    private String getStringTargeting(CreateUserRequestDto userDetails) {
        String jsonStringTargeting;
        try {
            TargetingRequestDto  targetingRequestDto = userDetails.getTargeting();
            TargetingRequestDto targetingDto = getTargetingDto(targetingRequestDto);

            ObjectMapper obj = new ObjectMapper();
            jsonStringTargeting = obj.writeValueAsString(targetingDto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON can't be processed");
        }
        return jsonStringTargeting;
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

    @Timed("Performance")
    private UserRepresentation searchKeyCloakAccountByUserName(String username) throws AccountNotFoundException {
        UserResource userResource = getInstanceKeycloak(AUTHURL, KC_PROXY_HOST, KC_PROXY_PORT, KC_IAM_CLI_CLIENT_ID,
                KC_IAM_CLI_CLIENT_SECRET).realm(REALM).users().get(username);

        if (userResource ==null){
            throw new AccountNotFoundException(username + "can not login");
        }else
        {
            try {
                return userResource.toRepresentation();

            }catch (NotFoundException ex){
                throw new AccountNotFoundException("Account not found",ex.getMessage());
            }catch (ForbiddenException e){
                throw new AccountNotFoundException("Account not found",e.getMessage());
            }

        }
    }

    @Override
    public UserRepresentation getUserByUsername(String userId)  throws AccountNotFoundException{
        UserRepresentation userRepresentation=null;
        UserResource userResource = getInstanceKeycloak(AUTHURL, KC_PROXY_HOST, KC_PROXY_PORT, KC_IAM_CLI_CLIENT_ID,
                KC_IAM_CLI_CLIENT_SECRET).realm(REALM).users().get(userId);

        if(userResource !=null)
            userRepresentation= userResource.toRepresentation();

        return userRepresentation;

    }

    @Override
    public UserDetails findByUsername(String username) throws Exception {
        UserDetails user = userDetailsRepository.findByUsername(username);
        if(user != null){
            UserData userData = userDataRepository.findByUserId(user.getId());
            user.setUserData(userData);
        }
        return user;
    }

    @Override
    public UserDetails findById(long id) throws Exception {
        UserDetails user = userDetailsRepository.findById(id);
        if(user != null){
            UserData userData = userDataRepository.findByUserId(user.getId());
            user.setUserData(userData);
        }
        return userDetailsRepository.findById(id);
    }

    @Override
    public UserDetails update(UserDetails userDetails) {
        updateKeycloakUser(userDetails);
        return userDetailsRepository.save(userDetails);
    }

    public void updateKeycloakUser(UserDetails userDetails){
        Keycloak keycloakInstance=  getInstanceKeycloak(AUTHURL, KC_PROXY_HOST, KC_PROXY_PORT, KC_IAM_CLI_CLIENT_ID,
                KC_IAM_CLI_CLIENT_SECRET);

        UserRepresentation user = keycloakInstance.realm(REALM).users().get(userDetails.getUserRefId()).toRepresentation();

        if(user !=null){
            if(!userDetails.getUsername().equals(user.getUsername()))
                user.setUsername(userDetails.getUsername());

            if(!userDetails.getFirstName().equals(user.getFirstName()))
                user.setFirstName(userDetails.getFirstName());

           if(!userDetails.getLastName().equals(user.getLastName()))
                user.setLastName(userDetails.getLastName());

            if(!userDetails.getEmail().equals(user.getEmail()))
                user.setEmail(userDetails.getEmail());

            keycloakInstance.realm(REALM).users().get(user.getId()).update(user);
        }

    }

    @Timed("Performance")
    public String createAccount(CreateUserRequestDto kcAccountDto) throws CreateAccountException {
        // Appel des apis d'admin de keycloak pour creation du compte
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(kcAccountDto.getPassword());
            UserRepresentation user = new UserRepresentation();
            user.setUsername(kcAccountDto.getUsername());
            user.setFirstName(kcAccountDto.getFirstName());
            user.setLastName(kcAccountDto.getLastName());
            user.setEmail(kcAccountDto.getEmail());
            // enabled by default
            user.setEnabled(true);

//            user.singleAttribute(Constante.KEYCLOAK_LOCALE_ATTRIBUTE, kcAccountDto.getAttribute().get(Constante.KEYCLOAK_LOCALE_ATTRIBUTE));
            user.setCredentials(Arrays.asList(credential));

            javax.ws.rs.core.Response response = getInstanceKeycloak(AUTHURL, KC_PROXY_HOST, KC_PROXY_PORT, KC_IAM_CLI_CLIENT_ID, KC_IAM_CLI_CLIENT_SECRET).realm(REALM).users().create(user);
            String errorMsg="";
            if (response.hasEntity())
            {
                ErrorRepresentation error = response.readEntity(ErrorRepresentation.class);
                errorMsg = error.getErrorMessage();
            }

            final int status = response.getStatus();

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Call KC Admin : POST /user, response : %d , %s", status,errorMsg));
            }
            if (status != org.apache.http.HttpStatus.SC_CREATED) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Unable to create account: %s : %s",kcAccountDto.getUsername(),errorMsg));
                }
                if (status == HttpStatus.SC_CONFLICT)
                {
                    throw new ConflictAccountException(errorMsg);
                }
                throw new CreateAccountException(KEYCLOAK_ERROR_MSG + status);
            }
            else
            {
                String path = response.getLocation().getPath();
                return path.substring(path.lastIndexOf('/') + 1);
            }
        }
        catch(CreateAccountException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            logger.error("Unable to create account.",e);
            throw new CreateAccountException("Unable to create account.",e);
        }
    }


    private Keycloak keycloakInstanceForLoginUser(){
        Keycloak instance = Keycloak.getInstance(AUTHURL, REALM,CLIENTID,SecurityContextUtils.getJWTToken());
        return instance;
    }

    private Keycloak getInstanceKeycloak (String url, String proxyHost, String proxyPort, String clientId, String clientSecret) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("getInstanceKeycloak with Parameters KC_PROXY_HOST:%s ,KC_PROXY_PORT:%s ,KC_CONNEXION_POOL_SIZE:%s, KC_CONNEXION_TTL:%s, clientId:%s", proxyHost ,proxyPort ,KC_CONNEXION_POOL_SIZE, KC_CONNEXION_TTL, clientId));
        }

        ResteasyClient rc;
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("New Connexion Keycloak, Conf Client KC URL: %s with Proxy %s , realm: %s", url,proxyHost, REALM));
        }
        if (!StringUtils.isEmpty(proxyHost))
            rc = new ResteasyClientBuilder().defaultProxy(proxyHost, Integer.parseInt(proxyPort)).connectionPoolSize(KC_CONNEXION_POOL_SIZE).connectionTTL(KC_CONNEXION_TTL, TimeUnit.SECONDS).build();
        else
            rc = new ResteasyClientBuilder().connectionPoolSize(KC_CONNEXION_POOL_SIZE).connectionTTL(KC_CONNEXION_TTL, TimeUnit.SECONDS).build();

        instanceKeycloak = KeycloakBuilder
                .builder()
                .serverUrl(url)
                .realm(REALM)
                .clientSecret(clientSecret)
                .clientId(clientId)
                .resteasyClient(rc)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        return instanceKeycloak;
    }

    @Override
    public Boolean passwordChange(PasswordUpdateRequestDto requestDto) {
        Boolean response=true;
        CredentialRepresentation creds = new CredentialRepresentation();
        creds.setType(CredentialRepresentation.PASSWORD);
        creds.setValue(requestDto.getNewPassword());
        creds.setTemporary(false);

        Keycloak keycloakInstance=  getInstanceKeycloak(AUTHURL, KC_PROXY_HOST, KC_PROXY_PORT, KC_IAM_CLI_CLIENT_ID,
                KC_IAM_CLI_CLIENT_SECRET);

        List<UserRepresentation> users = keycloakInstance.realm(REALM).users().search(requestDto.getUsername());
        if(users.size()==0) {
            response = false;
        }else{
            keycloakInstance.realm(REALM).users().get(users.get(0).getId()).resetPassword(creds);
        }
        return response;
    }

    @Override
    public Boolean deleteUser(DeleteUserRequestDto requestDto) {
        Boolean response=true;
        List<UserRepresentation> users=null;
        String userId=null;
        String username=null;

        Keycloak keycloakInstance=  getInstanceKeycloak(AUTHURL, KC_PROXY_HOST, KC_PROXY_PORT, KC_IAM_CLI_CLIENT_ID,
                KC_IAM_CLI_CLIENT_SECRET);

        if(requestDto.getUsername() !=null) {
            users = keycloakInstance.realm(REALM).users().search(requestDto.getUsername());
        }else{
            users= keycloakInstance.realm(REALM).users().search("email", requestDto.getEmail(), null, null, null, null);
        }

        if(users.size()==0) {
            response = false;
        }else{
            userId = users.get(0).getId();
            username = users.get(0).getUsername();
            keycloakInstance.realm(REALM).users().delete(userId);
        }

        if(response)
            deleteUser(username);

        return response;
    }

    public void deleteUser(String username){
      UserDetails userDetails= userDetailsRepository.findByUsername(username);

      if(userDetails !=null){
          userDetails.setEnabled(false);
          userDetailsRepository.save(userDetails);
      }
    }
}
