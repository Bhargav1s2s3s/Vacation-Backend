package com.vacation.core.dto.feed;

import lombok.Data;

@Data
public class CreateFeedRequest {
    private String caption;
    private int imageCount;
}
