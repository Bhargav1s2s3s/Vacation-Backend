package com.vacation.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "feed_images")
@Data
@NoArgsConstructor
public class FeedImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private FeedEntity feed;

    @Column(name = "image")
    private String image;
}
