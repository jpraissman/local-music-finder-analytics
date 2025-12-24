package com.thelocalmusicfinder.localmusicfinderanalytics.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryDetail {
  private String name;
  private int total;
  private int totalUnique;
  private int totalUniqueReturning;
  private int totalUniqueNew;
}
