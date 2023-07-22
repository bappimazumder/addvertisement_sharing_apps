package com.advertise.service.advertisement;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileStore {
    private final AmazonS3 amazonS3;
    /*@Value("${amazon.s3.bucket}")
    private String bucketName;*/
    public URL upload(String path,
                       String fileName,
                       Optional<Map<String, String>> optionalMetaData,
                       InputStream inputStream) {
        URL url = null;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            amazonS3.putObject(path, fileName, inputStream, objectMetadata);
            S3Object object = amazonS3.getObject(path, fileName);
            if(object != null){
                return generatePresignedURL(object.getBucketName(),object.getKey());
            }

        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = amazonS3.getObject(path, key);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    public void deleteObject(String bucketName, String fileName){
        /*for (S3ObjectSummary file : amazonS3.listObjects(bucketName, key).getObjectSummaries()){
            amazonS3.deleteObject(bucketName, file.getKey());
        }*/
        if(fileName !=null && bucketName != null){
            amazonS3.deleteObject(bucketName, fileName);
        }

    }

    public URL generatePresignedURL (String bucketName, String objectKey) throws IOException {
        URL url = null;
        try {
            // Set the presigned URL to expire after one hour.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            System.out.println("Pre-Signed URL: " + url.toString());
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to generate amazon file link", e);
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        return url;
    }
}



