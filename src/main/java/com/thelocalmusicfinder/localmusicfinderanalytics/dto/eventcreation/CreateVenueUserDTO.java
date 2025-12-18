package com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateVenueUserDTO {
    private UUID userId;
    private Long venueId;
}
