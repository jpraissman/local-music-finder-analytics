package com.thelocalmusicfinder.localmusicfinderanalytics.dto.session;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSessionDTO {
  @NotNull
  private UUID userId;

  private Long campaignId;

  private String referer;

  private String urlEntry;
}
