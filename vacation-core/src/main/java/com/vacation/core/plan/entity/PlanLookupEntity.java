package com.vacation.core.plan.entity;

import com.vacation.auth.profile.entity.ProfileEntity;

import com.vacation.common.enums.UserActionEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "plan_lookup")
@Data
@NoArgsConstructor
public class PlanLookupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Column(name = "tour_package_id")
    private UUID tourPackageId;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_action")
    private UserActionEnum userAction;
}
