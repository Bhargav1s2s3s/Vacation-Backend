package com.vacation.core.repository.user;

import com.vacation.core.entity.user.PlanLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanLookupRepository extends JpaRepository<PlanLookupEntity, UUID> {
}
