package com.vacation.core.shared.service;

public interface FileStorageService {

    /**
     * Generate a presigned upload URL for the given S3 object key.
     */
    String generatePresignedUploadUrl(String objectKey);

    /**
     * Delete an object from S3 by its object key.
     */
    void deleteObject(String objectKey);

    /**
     * Generate a presigned download URL for the given S3 object key.
     */
    String generatePresignedDownloadUrl(String objectKey);
}
