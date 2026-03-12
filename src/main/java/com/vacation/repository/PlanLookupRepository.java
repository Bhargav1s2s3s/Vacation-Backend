package com.vacation.repository;

import com.vacation.entity.PlanLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanLookupRepository extends JpaRepository<PlanLookupEntity, UUID> {
}
