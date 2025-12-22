package com.thelocalmusicfinder.localmusicfinderanalytics.dto.query;

import java.util.List;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseQueryResponseDTO {
  private int total;
  private int totalUnique;
  private List<QueryDetail> sublayerDetails;
}
