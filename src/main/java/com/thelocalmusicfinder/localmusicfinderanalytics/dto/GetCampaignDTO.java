package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

/**
 * DTO used to request the link of an event, we need the id of the campaign and the user id
 */
@Data
@SuperBuilder
public class GetCampaignDTO extends CreateUserDTO{
    private String url;
    private Optional<String> user_id;
}