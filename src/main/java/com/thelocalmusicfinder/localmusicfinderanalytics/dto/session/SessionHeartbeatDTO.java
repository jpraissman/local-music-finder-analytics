package com.thelocalmusicfinder.localmusicfinderanalytics.dto.session;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SessionHeartbeatDTO {
  @NotNull
  private UUID userId;

  @NotNull
  private String activityOverview;

  @NotNull
  private int numScrolls;
}
