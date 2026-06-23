package com.vacation.core.shared.dto;

import com.vacation.common.enums.MediaType;
import lombok.Data;

import java.util.UUID;

@Data
public class MediaUploadRequest {

    private MediaType mediaType;

    private UUID referenceId;
}
