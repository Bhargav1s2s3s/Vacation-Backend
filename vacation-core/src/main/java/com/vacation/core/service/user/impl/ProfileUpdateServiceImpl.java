package com.vacation.core.service.user.impl;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.ResourceNotFoundException;
import com.vacation.core.dto.user.ProfileUpdateRequest;
import com.vacation.core.dto.user.ProfileUpdateResponse;
import com.vacation.core.mapper.user.ProfileUpdateMapper;
import com.vacation.core.repository.shared.MediaRepository;
import com.vacation.core.service.user.ProfileUpdateService;
import com.vacation.core.shared.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileUpdateServiceImpl implements ProfileUpdateService {

    private final ProfileRepository profileRepository;
    private final ProfileUpdateMapper profileMapper;
    private final MediaRepository mediaRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public ProfileUpdateResponse updateProfile(UUID profileId, ProfileUpdateRequest request) {

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.PROFILE_NOT_FOUND, "Profile not found"));

        profileMapper.updateEntityFromRequest(request, profile);

        ProfileEntity updated = profileRepository.save(profile);
        log.info("Profile updated successfully for profileId: {}", profileId);

        return buildResponse(updated, "Profile updated successfully");
    }

    @Override
    public ProfileUpdateResponse getProfile(UUID profileId) {
        ProfileEntity ent = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.PROFILE_NOT_FOUND, "Profile not found"));
        log.info("Profile fetched successfully for profileId: {}", profileId);

        return buildResponse(ent, "Profile fetched successfully");
    }

    // ── helpers ──────────────────────────────────────────

    private ProfileUpdateResponse buildResponse(ProfileEntity entity, String message) {
        String imageUrl = resolveDownloadUrl(entity.getUserImage());

        return new ProfileUpdateResponse(
                entity.getId(),
                entity.getUserImage(),
                imageUrl,
                entity.getFirstName(),
                entity.getLastName(),
                entity.getLocation(),
                message
        );
    }

    /**
     * Resolve a userImage (media ID stored as string) to a presigned download URL.
     */
    private String resolveDownloadUrl(String mediaIdStr) {
        if (mediaIdStr == null || mediaIdStr.isBlank()) {
            return null;
        }
        try {
            UUID mediaId = UUID.fromString(mediaIdStr);
            return mediaRepository.findById(mediaId)
                    .map(m -> fileStorageService.generatePresignedDownloadUrl(m.getObjectKey()))
                    .orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
