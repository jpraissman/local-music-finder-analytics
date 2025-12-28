package com.thelocalmusicfinder.localmusicfinderanalytics.util;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.NameWithUserId;
import com.thelocalmusicfinder.localmusicfinderanalytics.domain.TotalNumbers;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.QueryDetail;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;

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

  public static TotalNumbers getTotalNumbers(Set<Session> sessions) {
    int total = sessions.size();
    int totalUnique = 0;
    int totalUniqueNew = 0;
    int totalUniqueReturning = 0;
    Set<UUID> uniqueIds = new HashSet<>();
    for (Session session: sessions) {
      totalUniqueNew += session.getIsUsersFirstSession() ? 1 : 0;
      totalUniqueReturning += session.getIsUsersFirstSession() ? 0 : 1;
      if (!uniqueIds.contains(session.getUser().getId())) {
        uniqueIds.add(session.getUser().getId());
        totalUnique++;
      }
    }
    int totalMinusUnique = total - totalUnique;
    totalUniqueReturning -= totalMinusUnique;

    return new TotalNumbers(total, totalUnique, totalUniqueNew, totalUniqueReturning);
  }

  public static List<QueryDetail> generateQueryDetailList(List<NameWithUserId> items) {
    Map<String, QueryDetailWithUniqueUsers> queryDetailsMap = new HashMap<>();
    for (NameWithUserId item : items) {
      int newCount = item.isNewSession() ? 1 : 0;
      int returningCount = item.isNewSession() ? 0 : 1;

      if (queryDetailsMap.containsKey(item.name())) {
        QueryDetailWithUniqueUsers queryDetailWithUniqueUsers = queryDetailsMap.get(item.name());
        QueryDetail queryDetail = queryDetailWithUniqueUsers.queryDetail();
        Set<UUID> uniqueUsers = queryDetailWithUniqueUsers.uniqueUsers();

        queryDetail.setTotal(queryDetail.getTotal() + 1);
        // we will take care of fixing these values at the end of the method
        queryDetail.setTotalUniqueNew(queryDetail.getTotalUniqueNew() + newCount);
        queryDetail.setTotalUniqueReturning(queryDetail.getTotalUniqueReturning() + returningCount);

        if (!uniqueUsers.contains(item.userId())) {
          queryDetail.setTotalUnique(queryDetail.getTotalUnique() + 1);
          uniqueUsers.add(item.userId());
        }
      } else {
        Set<UUID> uniqueUsers = new HashSet<>();
        uniqueUsers.add(item.userId());

        QueryDetail newQueryDetail = new QueryDetail(item.name(), 1, 1, returningCount, newCount);

        queryDetailsMap.put(item.name(), new QueryDetailWithUniqueUsers(newQueryDetail, uniqueUsers));
      }
    }

    List<QueryDetail> result = new ArrayList<>();
    for (QueryDetailWithUniqueUsers item : queryDetailsMap.values()) {
      QueryDetail queryDetail = item.queryDetail();
      int totalMinusUnique = queryDetail.getTotal() - queryDetail.getTotalUnique();
      // the totalUniqueReturning value should subtract this value to be correct
      queryDetail.setTotalUniqueReturning(queryDetail.getTotalUniqueReturning() - totalMinusUnique);
      result.add(queryDetail);
    }
    return result;
  }
}

record QueryDetailWithUniqueUsers(QueryDetail queryDetail, Set<UUID> uniqueUsers) {}