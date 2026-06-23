package com.vacation.core.controller.user;

import com.vacation.common.config.BaseController;
import com.vacation.core.dto.user.ProfileUpdateRequest;
import com.vacation.core.dto.user.ProfileUpdateResponse;
import com.vacation.core.service.user.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
public class VacationProfileController extends BaseController {

    private final ProfileUpdateService profileUpdateService;

    @PutMapping("/core/profile/{profileId}")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @PathVariable UUID profileId,
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileUpdateResponse response = profileUpdateService.updateProfile(profileId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/core/profile/{profileId}")
    public ResponseEntity<ProfileUpdateResponse> getProfile(@PathVariable UUID profileId) {
        ProfileUpdateResponse response = profileUpdateService.getProfile(profileId);
        return ResponseEntity.ok(response);
    }
}
