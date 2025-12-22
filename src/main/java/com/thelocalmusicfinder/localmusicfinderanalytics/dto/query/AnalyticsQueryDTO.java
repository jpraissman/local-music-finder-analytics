package com.thelocalmusicfinder.localmusicfinderanalytics.dto.query;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AnalyticsQueryDTO {
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  private String platform;

  private String subgroup;

  private String postMemo;

  private Boolean includeAdmin;

  private Integer minDurationInSec;
}
