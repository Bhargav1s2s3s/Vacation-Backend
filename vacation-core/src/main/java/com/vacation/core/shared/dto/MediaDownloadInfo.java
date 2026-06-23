package com.vacation.core.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MediaDownloadInfo {
    private UUID mediaId;
    private String downloadUrl;
}
