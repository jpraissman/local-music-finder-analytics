package com.thelocalmusicfinder.localmusicfinderanalytics.repositories;

import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CampaignUserEventRepository extends JpaRepository<CampaignUserEvent, Long> {
    List<CampaignUserEvent> findByTimestampBetween(Instant start, Instant end);
}
