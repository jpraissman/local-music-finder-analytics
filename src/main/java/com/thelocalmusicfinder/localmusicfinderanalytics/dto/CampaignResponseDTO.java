package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampaignResponseDTO {
    private String generatedLink;
}
