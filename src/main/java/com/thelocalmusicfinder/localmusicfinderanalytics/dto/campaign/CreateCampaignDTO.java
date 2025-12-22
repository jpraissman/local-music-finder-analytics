package com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateCampaignDTO {
  @NotBlank
  private String platform;

  @NotBlank
  private String subgroup;

  @NotBlank
  private String postMemo;
}
