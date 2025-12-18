package com.thelocalmusicfinder.localmusicfinderanalytics.domain;

import lombok.Data;

@Data
public class LocationInfo {
  private String locationId;
  private String formattedAddress;
  private String county;
  private String town;
}
