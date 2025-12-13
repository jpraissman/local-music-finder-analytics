package com.thelocalmusicfinder.localmusicfinderanalytics.dto;

import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateBandUserDTO {
    private UUID userId;
    private Long bandId;
}
