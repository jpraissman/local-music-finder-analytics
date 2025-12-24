package com.thelocalmusicfinder.localmusicfinderanalytics.repositories;

import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, Long> {
  @Query("""
    SELECT DISTINCT s
    FROM Session s
    JOIN FETCH s.campaign c
    JOIN FETCH s.user u
    WHERE s.sessionStart >= :start
      AND s.sessionStart < :end
      AND (:includeAdmins IS TRUE OR u.isAdmin = false)
      AND (:platform IS NULL OR c.platform = :platform)
      AND (:subgroup IS NULL OR c.subgroup = :subgroup)
      AND (:postMemo IS NULL OR c.postMemo = :postMemo)
    """)
  List<Session> findSessions(Instant start, Instant end, boolean includeAdmins,
                             String platform, String subgroup, String postMemo);

  /**
   * Finds session linked to user that is after the given time
   */
  Optional<Session> findFirstByUserIdAndLastSessionActivityAfterOrderByLastSessionActivityDesc(UUID userId, Instant cutoff);

  List<Session> findByUserId(UUID userId);
}
