package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.GetCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.GetLinkResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final CampaignService campaignService;

    @PostMapping("/campaign/get")
    public ResponseEntity<GetLinkResponseDTO> getCampaign(@Valid @RequestBody GetCampaignDTO payload) {
        try{
            GetLinkResponseDTO res = campaignService.getLink(payload);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
