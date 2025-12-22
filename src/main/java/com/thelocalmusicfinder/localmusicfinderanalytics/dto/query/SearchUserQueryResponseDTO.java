package com.thelocalmusicfinder.localmusicfinderanalytics.dto.query;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SearchUserQueryResponseDTO extends BaseQueryResponseDTO {
  private List<QueryDetail> counties;
  private List<QueryDetail> towns;
  private List<QueryDetail> formattedAddresses;
  private List<QueryDetail> searchContexts;
}
