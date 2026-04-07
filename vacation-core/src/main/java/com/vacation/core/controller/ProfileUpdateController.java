package com.vacation.core.controller;

import com.vacation.common.config.BaseController;
import com.vacation.core.dto.ProfileUpdateRequest;
import com.vacation.core.dto.ProfileUpdateResponse;
import com.vacation.core.service.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
public class ProfileUpdateController extends BaseController {

    private final ProfileUpdateService profileUpdateService;

    @PutMapping("/core/profile/{profileId}")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @PathVariable UUID profileId,
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileUpdateResponse response = profileUpdateService.updateProfile(profileId, request);
        return ResponseEntity.ok(response);
    }
}
