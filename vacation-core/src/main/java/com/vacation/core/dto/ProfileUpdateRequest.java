package com.vacation.core.dto;

import java.util.UUID;

public record ProfileUpdateRequest (
         String imageId,
         String firstName,
         String lastName,
         String location,
         String mobileNumber
) {
}
