package com.vacation.core.dto;

import java.util.UUID;

public record ProfileUpdateResponse(
        UUID profileId,
        String userImage,
        String firstName,
        String lastName,
        String location,
        String message
) {
}
