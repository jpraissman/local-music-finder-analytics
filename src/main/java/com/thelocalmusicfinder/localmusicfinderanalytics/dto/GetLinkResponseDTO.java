package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Returns link of requested campaign and user_id for current user.
 */
@Data
@Builder
@AllArgsConstructor
public class GetLinkResponseDTO {
    private String link;
    private UUID user_id;
}
