package com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.queryresponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignQueryResponseDTO {
  private List<QueryDetail> sublayerDetails;
  private List<QueryDetail> pathDetails;
}