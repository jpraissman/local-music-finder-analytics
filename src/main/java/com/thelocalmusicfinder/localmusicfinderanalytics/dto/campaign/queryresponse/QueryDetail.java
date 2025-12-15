package com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.queryresponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryDetail {
  private String name;
  private int totalUsers;
  private int totalUniqueUsers;
}
