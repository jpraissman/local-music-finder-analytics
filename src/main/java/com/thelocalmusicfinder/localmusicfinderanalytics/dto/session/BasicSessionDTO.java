package com.thelocalmusicfinder.localmusicfinderanalytics.dto.session;

import java.util.UUID;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BasicSessionDTO {
  private Long sessionId;
  private UUID userId;
  private int durationInSec;
  private String urlEntry;
  private String platform;
}
