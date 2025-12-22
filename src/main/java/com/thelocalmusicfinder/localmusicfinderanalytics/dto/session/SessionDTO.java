package com.thelocalmusicfinder.localmusicfinderanalytics.dto.session;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SessionDTO extends BasicSessionDTO {
  private String activityOverview;
}
