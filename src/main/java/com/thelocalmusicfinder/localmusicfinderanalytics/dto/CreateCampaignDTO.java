package com.thelocalmusicfinder.localmusicfinderanalytics.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCampaignDTO {
    private String platform;
    private String subgroup;
    private String postUrl;
    private String targetUrl;
}
