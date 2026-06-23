package com.vacation.core.shared.service;

import com.vacation.common.enums.MediaType;
import com.vacation.core.shared.dto.MediaDetailResponse;
import com.vacation.core.shared.dto.MediaDownloadInfo;
import com.vacation.core.shared.dto.MediaUploadRequest;
import com.vacation.core.shared.dto.MediaUploadResponse;

import java.util.List;
import java.util.UUID;

public interface MediaService {

    /**
     * Create a media record and generate a presigned upload URL.
     */
    MediaUploadResponse uploadMedia(MediaUploadRequest request);

    /**
     * Regenerate a presigned upload URL for an existing media.
     */
    MediaUploadResponse retryUpload(UUID mediaId);

    /**
     * Delete a media from S3 and the database.
     */
    void deleteMedia(UUID mediaId);

    /**
     * Get a single media by its ID (includes presigned download URL).
     */
    MediaDetailResponse getMedia(UUID mediaId);

    /**
     * Get all media for a given type and reference (e.g., all FEED media for a feedId).
     */
    List<MediaDetailResponse> getMediaByReference(MediaType mediaType, UUID referenceId);

    /**
     * Bulk generate presigned download URLs for a list of media IDs.
     */
    List<MediaDownloadInfo> getDownloadUrls(List<UUID> mediaIds);
}
