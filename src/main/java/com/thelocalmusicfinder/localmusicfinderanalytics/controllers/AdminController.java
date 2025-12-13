package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CampaignResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.GetCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.GetLinkResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.CampaignService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CampaignService campaignService;

    /**
     * Used to create a new campaign, used by C-level executives only
     * @param payload platform, group, postURL, and targetURL for new campaign
     * @return String of link to share
     */
    @PostMapping("/campaign/new")
    public ResponseEntity<String> createCampaign(@Valid @RequestBody CreateCampaignDTO payload) {
        try{
            String campResponse = campaignService.createCampaign(payload);
            return new  ResponseEntity<>(campResponse, HttpStatus.OK);
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }



}
