package com.vacation.core.shared.dto;

import com.vacation.common.enums.MediaType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MediaDetailResponse {
    private UUID mediaId;
    private MediaType mediaType;
    private UUID referenceId;
    private String objectKey;
    private String downloadUrl;
    private LocalDateTime createdAt;
}
