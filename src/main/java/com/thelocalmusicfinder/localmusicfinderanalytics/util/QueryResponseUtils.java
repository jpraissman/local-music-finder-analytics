package com.thelocalmusicfinder.localmusicfinderanalytics.util;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.NameWithUserId;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.QueryDetail;

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

  public static int getTotalUnique(List<UUID> userIds) {
    Set<UUID> uniqueIds = new HashSet<>();
    int total = 0;
    for (UUID uuid : userIds) {
      if (!uniqueIds.contains(uuid)) {
        uniqueIds.add(uuid);
        total++;
      }
    }
    return total;
  }

  public static List<QueryDetail> generateQueryDetailList(List<NameWithUserId> items) {
    Map<String, QueryDetailWithUniqueUsers> queryDetailsMap = new HashMap<>();
    for (NameWithUserId item : items) {
      if (queryDetailsMap.containsKey(item.name())) {
        QueryDetailWithUniqueUsers queryDetailWithUniqueUsers = queryDetailsMap.get(item.name());
        QueryDetail queryDetail = queryDetailWithUniqueUsers.queryDetail();
        Set<UUID> uniqueUsers = queryDetailWithUniqueUsers.uniqueUsers();

        if (uniqueUsers.contains(item.userId())) {
          queryDetail.setTotal(queryDetail.getTotal() + 1);
        } else {
          queryDetail.setTotal(queryDetail.getTotal() + 1);
          queryDetail.setTotalUnique(queryDetail.getTotalUnique() + 1);
          uniqueUsers.add(item.userId());
        }
      } else {
        Set<UUID> uniqueUsers = new HashSet<>();
        uniqueUsers.add(item.userId());

        QueryDetail newQueryDetail = new QueryDetail(item.name(), 1, 1);

        queryDetailsMap.put(item.name(), new QueryDetailWithUniqueUsers(newQueryDetail, uniqueUsers));
      }
    }

    List<QueryDetail> result = new ArrayList<>();
    for (QueryDetailWithUniqueUsers item : queryDetailsMap.values()) {
      result.add(item.queryDetail());
    }
    return result;
  }
}

record QueryDetailWithUniqueUsers(QueryDetail queryDetail, Set<UUID> uniqueUsers) {}
