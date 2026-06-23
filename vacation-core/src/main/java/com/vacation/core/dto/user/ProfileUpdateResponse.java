package com.vacation.core.dto.user;

import java.util.UUID;

public record ProfileUpdateResponse(
        UUID profileId,
        String userImage,
        String userImageUrl,
        String firstName,
        String lastName,
        String location,
        String message
) {
}
