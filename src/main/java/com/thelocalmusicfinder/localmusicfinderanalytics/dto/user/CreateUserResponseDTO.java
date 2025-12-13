package com.thelocalmusicfinder.localmusicfinderanalytics.dto.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserResponseDTO {
  private UUID userId;
}
