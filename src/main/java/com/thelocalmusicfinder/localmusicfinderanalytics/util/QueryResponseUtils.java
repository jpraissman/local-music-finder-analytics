package com.thelocalmusicfinder.localmusicfinderanalytics.util;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.NameWithSession;
import com.thelocalmusicfinder.localmusicfinderanalytics.domain.TotalNumbers;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.QueryDetail;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class QueryResponseUtils {

  public static Instant getStartInstant(AnalyticsQueryDTO query) {
    ZoneId eastern = ZoneId.of("America/New_York");
    return query.getStartDate().atStartOfDay(eastern).toInstant();
  }

  public static Instant getEndInstant(AnalyticsQueryDTO query) {
    ZoneId eastern = ZoneId.of("America/New_York");
    return query.getEndDate().plusDays(1).atStartOfDay(eastern).toInstant();
  }

  public static String getSublayerName(Campaign campaign, AnalyticsQueryDTO query) {
    if (query.getPlatform() == null) {
      return campaign.getPlatform();
    }
    if (query.getSubgroup() == null) {
      return campaign.getSubgroup();
    }
    if (query.getPostMemo() == null) {
      return campaign.getPostMemo();
    }
    return null;
  }

  public static TotalNumbers getTotalNumbers(List<Session> allSessions) {
    int total = allSessions.size();

    Set<Session> uniqueSessions = new HashSet<>(allSessions);
    int totalUnique = 0;
    int totalUniqueNew = 0;
    int totalUniqueMobile = 0;
    long totalDuration = 0;
    Set<UUID> uniqueIds = new HashSet<>();
    for (Session session: uniqueSessions) {
      totalDuration += Duration.between(session.getSessionStart(), session.getLastSessionActivity()).getSeconds();
      totalUniqueNew += session.getIsUsersFirstSession() ? 1 : 0;
      totalUniqueMobile += session.getUser().getDeviceType().equals("Phone") ? 1 : 0;
      if (!uniqueIds.contains(session.getUser().getId())) {
        uniqueIds.add(session.getUser().getId());
        totalUnique++;
      }
    }
    int totalUniqueReturning = totalUnique - totalUniqueNew;
    long avgDuration = totalUnique > 0 ? (totalDuration / totalUnique) : 0;

    return new TotalNumbers(total, totalUnique, totalUniqueNew, totalUniqueReturning, totalUniqueMobile, avgDuration);
  }

  public static List<QueryDetail> generateQueryDetailList(List<NameWithSession> allItems) {
    Map<String, QueryDetailWithUniqueUsers> queryDetailsMap = new HashMap<>();
    Map<String, Integer> nameToTotalCountMap = new HashMap<>();

    // generate full totals
    for (NameWithSession item : allItems) {
      if (nameToTotalCountMap.containsKey(item.name())) {
        nameToTotalCountMap.put(item.name(), nameToTotalCountMap.get(item.name()) + 1);
      } else {
        nameToTotalCountMap.put(item.name(), 1);
      }
    }

    // generate the unique details
    Set<NameWithSession> uniqueItems = new HashSet<>(allItems);
    for (NameWithSession item : uniqueItems) {
      int newCount = item.session().getIsUsersFirstSession() ? 1 : 0;
      int mobileCount = item.session().getUser().getDeviceType().equals("Phone") ? 1 : 0;
      long sessionDuration = Duration.between(item.session().getSessionStart(), item.session().getLastSessionActivity()).getSeconds();

      if (queryDetailsMap.containsKey(item.name())) {
        QueryDetailWithUniqueUsers queryDetailWithUniqueUsers = queryDetailsMap.get(item.name());
        QueryDetail queryDetail = queryDetailWithUniqueUsers.queryDetail();
        Set<UUID> uniqueUsers = queryDetailWithUniqueUsers.uniqueUsers();

        if (!uniqueUsers.contains(item.session().getUser().getId())) {
          queryDetail.setTotalUnique(queryDetail.getTotalUnique() + 1);
          queryDetail.setTotalUniqueNew(queryDetail.getTotalUniqueNew() + newCount);
          queryDetail.setTotalUniqueMobile(queryDetail.getTotalUniqueMobile() + mobileCount);
          queryDetail.setTotalUniqueDurationInSec(queryDetail.getTotalUniqueDurationInSec() + sessionDuration);
          uniqueUsers.add(item.session().getUser().getId());
        }
      } else {
        Set<UUID> uniqueUsers = new HashSet<>();
        uniqueUsers.add(item.session().getUser().getId());

        QueryDetail newQueryDetail = new QueryDetail(item.name(),
                nameToTotalCountMap.get(item.name()), 1, 0, newCount, mobileCount, sessionDuration);

        queryDetailsMap.put(item.name(), new QueryDetailWithUniqueUsers(newQueryDetail, uniqueUsers));
      }
    }

    // finalize totalUniqueReturning counts
    List<QueryDetail> result = new ArrayList<>();
    for (QueryDetailWithUniqueUsers item : queryDetailsMap.values()) {
      QueryDetail queryDetail = item.queryDetail();
      int totalUniqueReturning = queryDetail.getTotalUnique() - queryDetail.getTotalUniqueNew();
      queryDetail.setTotalUniqueReturning(totalUniqueReturning);
      result.add(queryDetail);
    }
    return result;
  }
}

record QueryDetailWithUniqueUsers(QueryDetail queryDetail, Set<UUID> uniqueUsers) {}