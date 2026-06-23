package com.vacation.core.service.feed;

import com.vacation.core.dto.feed.CreateFeedRequest;
import com.vacation.core.dto.feed.CreateFeedResponse;
import com.vacation.core.dto.feed.FeedDetailResponse;
import com.vacation.core.dto.feed.FeedSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeedService {

    CreateFeedResponse createFeed(UUID profileId, CreateFeedRequest request);

    FeedDetailResponse getFeedDetail(UUID feedId, UUID profileId);

    Page<FeedSummaryResponse> getFeeds(Pageable pageable);
}
