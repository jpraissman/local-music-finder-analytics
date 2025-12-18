package com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.campaign;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.QueryDetail;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignUserQueryResponseDTO {
  private int totalUsers;
  private int totalUniqueUsers;
  private List<QueryDetail> sublayerDetails;
  private List<QueryDetail> pathDetails;
}