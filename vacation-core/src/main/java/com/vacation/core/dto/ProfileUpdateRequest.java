package com.vacation.core.dto;


public record ProfileUpdateRequest (
         String imageId,
         String firstName,
         String lastName,
         String location
) {
}
