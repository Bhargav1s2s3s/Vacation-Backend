package com.vacation.core.service;

import com.vacation.core.dto.ProfileUpdateRequest;
import com.vacation.core.dto.ProfileUpdateResponse;

import java.util.UUID;

public interface ProfileUpdateService {

    ProfileUpdateResponse updateProfile(UUID profileId, ProfileUpdateRequest request);
}
