package com.vacation.core.service.user;

import com.vacation.core.dto.user.ProfileUpdateRequest;
import com.vacation.core.dto.user.ProfileUpdateResponse;

import java.util.UUID;

public interface ProfileUpdateService {

    ProfileUpdateResponse updateProfile(UUID profileId, ProfileUpdateRequest request);

    ProfileUpdateResponse getProfile(UUID profileId);
}
