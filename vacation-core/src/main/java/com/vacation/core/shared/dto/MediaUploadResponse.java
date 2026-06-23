package com.vacation.core.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MediaUploadResponse {
    private UUID mediaId;
    private String objectKey;
    private String uploadUrl;
}
