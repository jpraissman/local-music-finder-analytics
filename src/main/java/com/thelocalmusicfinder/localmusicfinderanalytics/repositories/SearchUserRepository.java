package com.thelocalmusicfinder.localmusicfinderanalytics.repositories;

import com.thelocalmusicfinder.localmusicfinderanalytics.models.SearchUserEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SearchUserRepository extends JpaRepository<SearchUserEvent, Long> {
  @Query("""
    SELECT DISTINCT e
    FROM SearchUserEvent e
    JOIN FETCH e.session s
    JOIN FETCH e.campaign c
    JOIN FETCH e.user u
    WHERE e.timestamp >= :start
      AND e.timestamp < :end
      AND (:includeAdmins IS TRUE OR u.isAdmin = false)
      AND (:platform IS NULL OR c.platform = :platform)
      AND (:subgroup IS NULL OR c.subgroup = :subgroup)
      AND (:postMemo IS NULL OR c.postMemo = :postMemo)
    """)
  List<SearchUserEvent> findEvents(Instant start, Instant end, boolean includeAdmins,
                                   String platform, String subgroup, String postMemo);
}
