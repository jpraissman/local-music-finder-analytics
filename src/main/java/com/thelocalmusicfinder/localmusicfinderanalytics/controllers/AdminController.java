package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.SearchUserQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.SessionQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.CampaignService;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.SessionService;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.event.SearchUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CampaignService campaignService;
    private final SessionService sessionService;
    private final SearchUserService searchUserService;

    /**
     * Used to create a new campaign
     * @param payload platform, group, and postMemo for new campaign
     * @return campaign id
     */
    @PostMapping(
            value = "/campaign",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CreateCampaignResponseDTO> findOrCreateCampaign(@Valid @RequestBody CreateCampaignDTO payload) {
      Campaign campaign = campaignService.findOrCreateCampaign(payload);
      CreateCampaignResponseDTO response = new CreateCampaignResponseDTO(campaign.getId());
      return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignDTO>> getAllCampaigns() {
      List<CampaignDTO> response = campaignService.getAllCampaigns();
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value="/query/session")
    public ResponseEntity<SessionQueryResponseDTO> campaignUserQuery(@Valid @RequestBody AnalyticsQueryDTO payload) {
        SessionQueryResponseDTO responseDTO = sessionService.query(payload);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/query/search-user")
    public ResponseEntity<SearchUserQueryResponseDTO> searchUserQuery(@Valid @RequestBody AnalyticsQueryDTO payload) {
      SearchUserQueryResponseDTO responseDTO = searchUserService.query(payload);
      return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/query/session/{sessionId}/logs")
    public ResponseEntity<String> getSessionLogs(@PathVariable("sessionId") Long sessionId) {
      String sessionLogs = sessionService.getSessionLogs(sessionId);
      return ResponseEntity.status(HttpStatus.OK).body(sessionLogs);
    }
}
