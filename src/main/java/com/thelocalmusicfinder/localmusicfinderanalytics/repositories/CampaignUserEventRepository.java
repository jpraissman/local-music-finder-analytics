package com.thelocalmusicfinder.localmusicfinderanalytics.repositories;

import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignUserEventRepository extends JpaRepository<CampaignUserEvent, Long> {
}
