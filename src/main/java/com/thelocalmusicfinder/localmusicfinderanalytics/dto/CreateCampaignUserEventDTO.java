package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateCampaignUserEventDTO {
  @NotNull
  private UUID userId;

  @NotNull
  private Long  campaignId;

  @NotBlank
  private String url;
}
