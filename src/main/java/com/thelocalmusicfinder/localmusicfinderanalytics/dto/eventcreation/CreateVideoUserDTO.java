package com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateVideoUserDTO {
    private UUID userId;
    private String videoId;
}
