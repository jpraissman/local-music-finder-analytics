package com.thelocalmusicfinder.localmusicfinderanalytics.dto.query;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.session.BasicSessionDTO;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SessionQueryResponseDTO extends BaseQueryResponseDTO {
  private List<QueryDetail> pathDetails;
  private List<BasicSessionDTO> sessions;
}