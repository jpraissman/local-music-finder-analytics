package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateCampaignUserEventDTO {
    private UUID userId;
    private Long  campaignId;
    private String url;
}
