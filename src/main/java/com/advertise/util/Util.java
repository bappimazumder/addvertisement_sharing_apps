package com.advertise.util;

import com.advertise.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class Util {

    private static ModelMapper modelMapper = new ModelMapper();

    public static <U, V> V convertClass(U mapperObject, Class<V> targetClass) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(mapperObject, targetClass);
    }

    public static <Source, Dest> void copyProperty(Source source, Dest target) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.map(source, target);
    }

    public static <U, V> List<V> toDtoList(List<U> mapperObjects, Class<V> targetClass) {
        List<V> dtoObjects = mapperObjects
                .stream()
                .map(u -> convertClass(u, targetClass))
                .collect(Collectors.toList());

        return dtoObjects;
    }

    public static String convertObjToString(Object clsObj) {  //convert object  to string json
        String jsonSender = new Gson().toJson(clsObj, new TypeToken<Object>() {
        }.getType());
        return jsonSender;
    }

    public static boolean isEmpty(String str)
    {
        if (str==null)
            return true;

        str=str.trim();
        if(str.isEmpty())
            return true;

        return false;

    }

    public static String toDateStringFromDate(Date date, DateFormat dateFormat) {
        if (date == null) return null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(dateFormat.getValue());
            return fmt.format(date);
        } catch (Exception ex) {
        }
        return null;
    }

    public static String toDateStringFromLocalDate(LocalDate date, DateFormat dateFormat) {
        if (date == null) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat.toString());
            return date.format(formatter);
        } catch (Exception ex) {
            return null;
        }
    }

    public enum DateFormat {
        DD_MM_YYYY_SLASH("dd/MM/yyyy"),
        MM_DD_YYYY_SLASH("MM/dd/yyyy"),
        YYYY_MM_DD_SLASH("yyyy/MM/dd"),
        YYYY_MM_DDTHH_MM_SS_SSS_DASH("yyyy-MM-dd'T'HH:mm:ss.SSS");

        private String value;

        DateFormat(String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }
    }

    public static <U, V> List<V> toDtoList(Iterable<U> mapperObjects, Class<V> targetClass) {
        List<V> dtoObjects = new ArrayList<>();

        mapperObjects.forEach(object -> {
            dtoObjects.add(convertClass(object, targetClass));
        });

        return dtoObjects;
    }

    public static boolean isEqualIgnoredCaseStr(String str1, String str2)
    {
        if(str1==null && str2==null)
            return  true;
        return str1.equalsIgnoreCase(str2);
    }

    public static boolean isEqual(Long v1, Long v2)
    {
        if(v1==null && v2==null)
            return  true;
        return v1.equals(v2);
    }

    public static Date getStartOfDay(Date date) {
        Long time = date.getTime();
        return new Date(time - time % (24 * 60 * 60 * 1000));
    }

    public static Date getEndOfDay(Date date) {
        return new Date(date.getTime() + 24 * 60 * 60 * 1000);
    }

    public static Date getMidOfDay(Date date) {
        long startTime = getStartOfDay(date).getTime();
        return new Date(startTime + 12 * 60 * 60 * 1000);
    }

    public static boolean isDateValid(Date date) {
        try {
            Date today = new Date();
            today = Util.getStartOfDay(today);

            if (date.compareTo(today) >= 0) return true;
            return false;
        } catch (Exception e) {
            log.error("Error " + e.getMessage());
            return false;
        }
    }

    public static String[] toArray(List<String> strings) {
        if (strings == null || strings.size() == 0) {
            return new String[0];
        }
        return strings.stream().toArray(String[]::new);
    }

    public static Date localDateToDateConverter(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

//    public static ServiceResponse requestErrorHandler(BindingResult bindingResult) {
//        ServiceResponse response =new ServiceResponse();
//        List<ErrorModel> errorModelList = new ArrayList();
//
//        for(FieldError fieldError : bindingResult.getFieldErrors()) {
//            ErrorModel errorModel = new ErrorModel();
//            errorModel.setField(fieldError.getField());
//            errorModel.setMessage(fieldError.getDefaultMessage());
//            errorModel.setDescription(fieldError.getObjectName());
//            errorModelList.add(errorModel);
//        }
//
//        response.setErrorList(errorModelList);
//        response.setStatus(HttpStatus.BAD_REQUEST);
//        response.setStatusCode(StatusCode.ERROR);
//        return response;
//    }


    public static boolean isValidProjOperation()
    {
        //TODO: need to design this feature,
        return true;
    }
    public static LocalDateTime convertToUtcDate(LocalDateTime ldt, String timeZone)
    {
        try
        {
            ZoneId zoneLocal=ZoneId.of(timeZone);
            ZoneId zoneUtc= ZoneId.of("UTC");
            ZonedDateTime zonedDT=ZonedDateTime.of(ldt, zoneLocal);
            ZonedDateTime utcDt=zonedDT.withZoneSameInstant(zoneUtc);
            String dtStr=utcDt.format(DateTimeFormatter.ofPattern(Defs.DATE_FORMAT_SIMPLE));
            return LocalDateTime.parse(dtStr);
        }
        catch (Exception e)
        {
            log.error("Err-DateTime err:"+e.getMessage());
            return ldt;
        }
    }



    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime)
    {
        LocalDate localDate=localDateTime.toLocalDate();
        return localDate.atStartOfDay();
    }

    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime)
    {
        LocalDate localDate=localDateTime.toLocalDate();
        return localDate.atTime(LocalTime.MAX);
    }

    public static boolean isProperJson(String str)
    {
        if(str==null || str.isEmpty())
            return false;
        try
        {
            new JSONObject(str);
        }
        catch (Exception e)
        {
            try
            {
                new JSONArray(str);
            }
            catch (Exception eArr)
            {
                log.fatal("deserialization-error:"+ eArr);
                return false;
            }
        }
        return true;
    }

    public static <T> JSONObject getSerializedJson(T obj)
    {
        JSONObject serializedJson= new JSONObject();

        try
        {
            ObjectMapper mapper= new ObjectMapper();
            serializedJson=new JSONObject(mapper.writeValueAsString(obj));
        }
        catch (Exception e)
        {
            log.info("serialization-error:"+ e);
        }

        return serializedJson;
    }

    public static <T> T getDeserializedObj(JSONObject jsonObject, Class<T>clazz)
    {
        T derializedClassObj=  null;

        ObjectMapper objectMapper= new ObjectMapper();
        try
        {
            derializedClassObj= objectMapper.readValue(jsonObject.toString(), clazz);
        }
        catch (Exception e)
        {
            log.error("deserialization-error:"+ e.getMessage());

        }
        return derializedClassObj;
    }

    public static <T> T getDeserializedObj(String str, Class<T>clazz)
    {
        T derializedClassObj=  null;

       /* if(!isProperJson(str))
            return derializedClassObj;*/
        ObjectMapper objectMapper= new ObjectMapper();
        try
        {
            // JSONObject jsonObject=new JSONObject(str);
            derializedClassObj= objectMapper.readValue(str, clazz);
        }
        catch (Exception e)
        {
            log.error("deserialization-error:"+ e.getMessage());

        }
        return derializedClassObj;
    }





    private static boolean isFieldToSkip(Field field)
    {
        if(field.getName().equalsIgnoreCase("teamMemberList")||
                field.getName().equalsIgnoreCase("actionStatusList") ||
                field.getName().equalsIgnoreCase("riskList")||
                field.getType().getSimpleName().equalsIgnoreCase("categoryList") ||
                field.getType().getSimpleName().equalsIgnoreCase("List")
        )
        {
            return true;
        }
        return false;
    }


    public static boolean validateEmail(String email) {

        final String emailValidateRegex = "^[a-zA-Z\\d_.+-]{1,30}@[a-z]{1,10}(?:\\.[a-z]{2,10}){1,6}$";
        final Pattern pattern = Pattern.compile(emailValidateRegex);

        final Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            return false;
        }

        return true;
    }

    public static UserSession setUserSession(HttpServletRequest req, AuthTokenInfo tokenInfo) {

        return null;
    }

    public static String isoDateTimeToStringDateTime( String datetime ){
       // String datetime = "2021-12-16T16:22:34";
        LocalDateTime source = LocalDateTime.parse(datetime,DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String AUTH_DATE_PATTERN = "dd/MM/yyyy'T'HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AUTH_DATE_PATTERN);
        String dateStr = source.format(formatter);
        return dateStr;
    }
}