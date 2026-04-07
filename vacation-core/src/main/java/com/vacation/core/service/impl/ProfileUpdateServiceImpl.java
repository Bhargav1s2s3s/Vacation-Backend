package com.vacation.core.service.impl;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.auth.repository.ProfileRepository;
import com.vacation.common.error.code.VacationErrorCode;
import com.vacation.common.error.exception.ResourceNotFoundException;
import com.vacation.core.dto.ProfileUpdateRequest;
import com.vacation.core.dto.ProfileUpdateResponse;
import com.vacation.core.mapper.ProfileUpdateMapper;
import com.vacation.core.service.ProfileUpdateService;
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
    private final ProfileUpdateMapper profileUpdateMapper;

    @Transactional
    @Override
    public ProfileUpdateResponse updateProfile(UUID profileId, ProfileUpdateRequest request) {

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        VacationErrorCode.PROFILE_NOT_FOUND, "Profile not found"));

        profileUpdateMapper.updateEntityFromRequest(request, profile);

        ProfileEntity updated = profileRepository.save(profile);
        log.info("Profile updated successfully for profileId: {}", profileId);

        return profileUpdateMapper.toResponse(updated);
    }
}
