package com.vacation.core.dto.feed;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FeedMediaUploadInfo {
    private UUID mediaId;
    private String uploadUrl;
}
