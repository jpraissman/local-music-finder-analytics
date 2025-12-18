package com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSearchUserEventDTO {
  @NotNull
  private UUID userId;

  @NotNull
  private Long campaignId;

  @NotBlank
  private String locationId;

  @NotBlank
  private String searchContext;
}
