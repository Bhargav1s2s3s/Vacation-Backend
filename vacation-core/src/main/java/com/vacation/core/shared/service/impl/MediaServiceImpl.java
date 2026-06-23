package com.vacation.core.shared.service.impl;

import com.vacation.common.enums.MediaType;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.ResourceNotFoundException;
import com.vacation.core.repository.shared.MediaRepository;
import com.vacation.core.shared.dto.MediaDetailResponse;
import com.vacation.core.shared.dto.MediaDownloadInfo;
import com.vacation.core.shared.dto.MediaUploadRequest;
import com.vacation.core.shared.dto.MediaUploadResponse;
import com.vacation.core.shared.entity.MediaEntity;
import com.vacation.core.shared.service.FileStorageService;
import com.vacation.core.shared.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public MediaUploadResponse uploadMedia(MediaUploadRequest request) {
        MediaEntity media = new MediaEntity();
        media.setId(UUID.randomUUID());
        media.setMediaType(request.getMediaType());
        media.setReferenceId(request.getReferenceId());

        String objectKey = request.getMediaType().name().toLowerCase() + "s/" + media.getId();
        media.setObjectKey(objectKey);

        mediaRepository.save(media);
        log.info("Media created: id={}, type={}, referenceId={}", media.getId(), request.getMediaType(), request.getReferenceId());

        String uploadUrl = fileStorageService.generatePresignedUploadUrl(objectKey);

        return MediaUploadResponse.builder()
                .mediaId(media.getId())
                .objectKey(objectKey)
                .uploadUrl(uploadUrl)
                .build();
    }

    @Override
    public MediaUploadResponse retryUpload(UUID mediaId) {
        MediaEntity media = findMediaOrThrow(mediaId);

        String uploadUrl = fileStorageService.generatePresignedUploadUrl(media.getObjectKey());
        log.info("Retry upload URL generated for mediaId: {}", mediaId);

        return MediaUploadResponse.builder()
                .mediaId(media.getId())
                .objectKey(media.getObjectKey())
                .uploadUrl(uploadUrl)
                .build();
    }

    @Transactional
    @Override
    public void deleteMedia(UUID mediaId) {
        MediaEntity media = findMediaOrThrow(mediaId);

        fileStorageService.deleteObject(media.getObjectKey());
        log.info("Deleted S3 object for mediaId: {}", mediaId);

        mediaRepository.delete(media);
        log.info("Deleted media row: {}", mediaId);
    }

    @Override
    public MediaDetailResponse getMedia(UUID mediaId) {
        MediaEntity media = findMediaOrThrow(mediaId);
        return toDetailResponse(media);
    }

    @Override
    public List<MediaDetailResponse> getMediaByReference(MediaType mediaType, UUID referenceId) {
        return mediaRepository.findByMediaTypeAndReferenceId(mediaType, referenceId)
                .stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaDownloadInfo> getDownloadUrls(List<UUID> mediaIds) {
        return mediaRepository.findAllById(mediaIds)
                .stream()
                .map(media -> MediaDownloadInfo.builder()
                        .mediaId(media.getId())
                        .downloadUrl(fileStorageService.generatePresignedDownloadUrl(media.getObjectKey()))
                        .build())
                .collect(Collectors.toList());
    }

    // ── helpers ──────────────────────────────────────────

    private MediaEntity findMediaOrThrow(UUID mediaId) {
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.MEDIA_NOT_FOUND, "Media not found"));
    }

    private MediaDetailResponse toDetailResponse(MediaEntity media) {
        return MediaDetailResponse.builder()
                .mediaId(media.getId())
                .mediaType(media.getMediaType())
                .referenceId(media.getReferenceId())
                .objectKey(media.getObjectKey())
                .downloadUrl(fileStorageService.generatePresignedDownloadUrl(media.getObjectKey()))
                .createdAt(media.getCreatedAt())
                .build();
    }
}
