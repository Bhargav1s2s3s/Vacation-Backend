package com.vacation.core.mapper.user;

import com.vacation.auth.entity.ProfileEntity;
import com.vacation.core.dto.user.ProfileUpdateRequest;
import com.vacation.core.dto.user.ProfileUpdateResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileUpdateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "emailId", ignore = true)
    @Mapping(target = "userImage", source = "imageId")
    void updateEntityFromRequest(ProfileUpdateRequest request, @MappingTarget ProfileEntity entity);

    @Mapping(target = "profileId", source = "entity.id")
    @Mapping(target = "message", source = "message")
    ProfileUpdateResponse toResponse(ProfileEntity entity, String message);
}
