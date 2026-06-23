package com.vacation.core.dto.user;


public record ProfileUpdateRequest (
         String imageId,
         String firstName,
         String lastName,
         String location
) {
}
