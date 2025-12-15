package com.thelocalmusicfinder.localmusicfinderanalytics.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Data;

@Component
@RequestScope
@Data
public class UserContext {
  private String userAgent;
  private String deviceClass;
  private String ipAddress;
  private boolean isBot;
}
