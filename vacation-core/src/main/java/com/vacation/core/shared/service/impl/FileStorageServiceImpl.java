package com.vacation.core.shared.service.impl;

import com.vacation.core.shared.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.region:ap-south-1}")
    private String region;

    @Value("${storage.endpoint:}")
    private String endpoint;

    @Value("${storage.access-key:}")
    private String accessKey;

    @Value("${storage.secret-key:}")
    private String secretKey;

    @Value("${storage.presign-expiry-minutes:15}")
    private long presignExpiryMinutes;

    private S3Client s3Client;
    private S3Presigner s3Presigner;

    @PostConstruct
    void init() {
        boolean isCustomEndpoint = endpoint != null && !endpoint.isBlank();

        if (isCustomEndpoint) {
            // LocalStack or S3-compatible service
            StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                            accessKey.isBlank() ? "test" : accessKey,
                            secretKey.isBlank() ? "test" : secretKey
                    )
            );

            s3Client = S3Client.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region))
                    .credentialsProvider(credentials)
                    .forcePathStyle(true)
                    .build();

            s3Presigner = S3Presigner.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region))
                    .credentialsProvider(credentials)
                    .serviceConfiguration(
                            S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build()
                    )
                    .build();

            log.info("FileStorageService initialized with custom endpoint: {}", endpoint);
        } else {
            // Real AWS — uses default credential chain (env vars, IAM role, etc.)
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            s3Presigner = S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            log.info("FileStorageService initialized with real AWS S3, region: {}", region);
        }
    }

    @Override
    public String generatePresignedUploadUrl(String objectKey) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignExpiryMinutes))
                .putObjectRequest(putRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        String url = presigned.url().toString();

        log.info("Generated presigned upload URL for key: {}", objectKey);
        return url;
    }

    @Override
    public void deleteObject(String objectKey) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteRequest);
        log.info("Deleted S3 object: {}", objectKey);
    }

    @Override
    public String generatePresignedDownloadUrl(String objectKey) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignExpiryMinutes))
                .getObjectRequest(getRequest)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
        String url = presigned.url().toString();

        log.info("Generated presigned download URL for key: {}", objectKey);
        return url;
    }
}
