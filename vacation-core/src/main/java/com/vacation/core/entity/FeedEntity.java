package com.vacation.core.entity;

import com.vacation.auth.entity.ProfileEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "feed")
@Data
@NoArgsConstructor
public class FeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Column(name = "caption")
    private String caption;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "comments")
    private String comments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<FeedImageEntity> images;
}
