package com.vacation.core.dto.feed;

import com.vacation.core.shared.dto.MediaDownloadInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FeedSummaryResponse {
    private UUID feedId;
    private UUID profileId;
    private String firstName;
    private String lastName;
    private String userImage;
    private String userImageUrl;
    private String caption;
    private Integer likeCount;
    private Integer commentCount;
    private List<MediaDownloadInfo> media;
    private LocalDateTime createdAt;
}
