package com.vacation.core.shared.controller;

import com.vacation.common.config.BaseController;
import com.vacation.common.enums.MediaType;
import com.vacation.core.shared.dto.MediaDetailResponse;
import com.vacation.core.shared.dto.MediaDownloadInfo;
import com.vacation.core.shared.dto.MediaUploadRequest;
import com.vacation.core.shared.dto.MediaUploadResponse;
import com.vacation.core.shared.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class MediaController extends BaseController {

    private final MediaService mediaService;

    /**
     * Upload a new media — creates a DB row and returns a presigned upload URL.
     * POST /mahe/vacation/core/media
     */
    @PostMapping("/core/media-upload")
    public ResponseEntity<MediaUploadResponse> uploadMedia(@RequestBody MediaUploadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.uploadMedia(request));
    }

    /**
     * Retry upload — regenerates a presigned URL for an existing media.
     * POST /mahe/vacation/core/media/{mediaId}/retry
     */
    @PostMapping("/core/media/{mediaId}/retry")
    public ResponseEntity<MediaUploadResponse> retryUpload(@PathVariable UUID mediaId) {
        return ResponseEntity.ok(mediaService.retryUpload(mediaId));
    }

    /**
     * Delete a media from S3 and the database.
     * DELETE /mahe/vacation/core/media/{mediaId}
     */
    @DeleteMapping("/core/media/{mediaId}")
    public ResponseEntity<Void> deleteMedia(@PathVariable UUID mediaId) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get a single media by its ID (includes presigned download URL).
     * GET /mahe/vacation/core/media/{mediaId}
     */
    @GetMapping("/core/media/{mediaId}")
    public ResponseEntity<MediaDetailResponse> getMedia(@PathVariable UUID mediaId) {
        return ResponseEntity.ok(mediaService.getMedia(mediaId));
    }

    /**
     * Get all media for a given type and reference.
     * GET /mahe/vacation/core/media?mediaType=FEED&referenceId=xxx
     */
    @GetMapping("/core/media")
    public ResponseEntity<List<MediaDetailResponse>> getMediaByReference(
            @RequestParam MediaType mediaType,
            @RequestParam UUID referenceId) {
        return ResponseEntity.ok(mediaService.getMediaByReference(mediaType, referenceId));
    }

    /**
     * Bulk generate presigned download URLs for a list of media IDs.
     * POST /mahe/vacation/core/media/download-urls
     * Body: ["uuid1", "uuid2", ...]
     */
    @PostMapping("/core/media/download-urls")
    public ResponseEntity<List<MediaDownloadInfo>> getDownloadUrls(@RequestBody List<UUID> mediaIds) {
        return ResponseEntity.ok(mediaService.getDownloadUrls(mediaIds));
    }
}
