package com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.searchuser;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.QueryDetail;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserQueryResponseDTO {
  private int totalCustomSearches;
  private int totalUniqueUsersWhoSearched;
  private List<QueryDetail> counties;
  private List<QueryDetail> towns;
  private List<QueryDetail> formattedAddresses;
}
