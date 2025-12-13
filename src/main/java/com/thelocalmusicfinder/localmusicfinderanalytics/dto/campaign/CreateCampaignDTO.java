package com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCampaignDTO {
  @NotBlank
  private String platform;

  @NotBlank
  private String subgroup;

  @NotBlank
  private String postMemo;
}
