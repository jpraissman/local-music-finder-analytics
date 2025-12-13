package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CampaignQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.QueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.QueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignUserEventRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.CampaignService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CampaignService campaignService;

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
    public ResponseEntity<CreateCampaignResponseDTO> createCampaign(@Valid @RequestBody CreateCampaignDTO payload) {
      Long campaignId = campaignService.createCampaign(payload);
      CreateCampaignResponseDTO response = new CreateCampaignResponseDTO(campaignId);
      return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // we want a query endpoint here to filter campaignuserevents by date, platform, post, etc
    @PostMapping(value="/query")
    ResponseEntity<CampaignQueryResponseDTO> query(@Valid @RequestBody QueryDTO payload) {
        return campaignService.campaignQuery(payload);
    };


}
