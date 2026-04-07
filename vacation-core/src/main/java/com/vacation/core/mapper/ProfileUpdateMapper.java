package com.vacation.core.mapper;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.core.dto.ProfileUpdateRequest;
import com.vacation.core.dto.ProfileUpdateResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileUpdateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "emailId", ignore = true)
    @Mapping(target = "userImage", source = "imageId")
    void updateEntityFromRequest(ProfileUpdateRequest request, @MappingTarget ProfileEntity entity);

    @Mapping(target = "profileId", source = "id")
    @Mapping(target = "message", constant = "Profile updated successfully")
    ProfileUpdateResponse toResponse(ProfileEntity entity);
}
