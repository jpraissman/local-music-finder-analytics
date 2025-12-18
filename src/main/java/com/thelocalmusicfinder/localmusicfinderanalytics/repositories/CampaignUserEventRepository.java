package com.thelocalmusicfinder.localmusicfinderanalytics.repositories;

import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CampaignUserEventRepository extends JpaRepository<CampaignUserEvent, Long> {
  @Query("""
    SELECT DISTINCT e
    FROM CampaignUserEvent e
    JOIN FETCH e.campaign c
    JOIN FETCH e.user u
    WHERE e.timestamp >= :start
      AND e.timestamp < :end
      AND u.isAdmin = false
      AND (:platform IS NULL OR c.platform = :platform)
      AND (:subgroup IS NULL OR c.subgroup = :subgroup)
      AND (:postMemo IS NULL OR c.postMemo = :postMemo)
    """)
  List<CampaignUserEvent> findEvents(Instant start, Instant end, String platform, String subgroup, String postMemo);
}
