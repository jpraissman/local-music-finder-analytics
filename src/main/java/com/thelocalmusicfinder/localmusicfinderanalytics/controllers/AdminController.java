package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.queryresponse.CampaignQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CampaignUserQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.CampaignService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CreateCampaignResponseDTO> findOrCreateCampaign(@Valid @RequestBody CreateCampaignDTO payload) {
      Long campaignId = campaignService.findOrCreateCampaign(payload);
      CreateCampaignResponseDTO response = new CreateCampaignResponseDTO(campaignId);
      return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // filter campaignuserevents by date, platform, post, etc
    @PostMapping(value="/campaign/query")
    public ResponseEntity<CampaignQueryResponseDTO> query(@Valid @RequestBody CampaignUserQueryDTO payload) {
        CampaignQueryResponseDTO responseDTO = campaignService.campaignQuery(payload);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }


}
