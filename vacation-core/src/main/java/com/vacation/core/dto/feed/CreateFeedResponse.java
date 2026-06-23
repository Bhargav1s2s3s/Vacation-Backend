package com.vacation.core.dto.feed;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateFeedResponse {
    private UUID feedId;
    private List<FeedMediaUploadInfo> images;
}
