package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class QueryDTO {
    private Instant startTime;
    private Instant endTime;
    // if no platform given, show all platforms
    private String platform;
    // if no subgroup given, show all posts within platform
    private String subgroup;
    // if no subgroup given, show all postmemos
    private String postMemo;
}
