package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CampaignQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateCampaignUserEventDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.QueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;

import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignUserEventRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final CampaignUserEventRepository campaignUserEventRepository;

    @Transactional
    public Long createCampaign(CreateCampaignDTO payload) {
        Campaign campaign = new Campaign();
        campaign.setPlatform(payload.getPlatform());
        campaign.setSubgroup(payload.getSubgroup());
        campaign.setPostMemo(payload.getPostMemo());
        Campaign createdCampaign = campaignRepository.save(campaign);
        return createdCampaign.getId();
    }

    @Transactional
    public void createCampaignUserEvent(CreateCampaignUserEventDTO payload) {
        CampaignUserEvent campaignUserEvent = new CampaignUserEvent();

        Optional<Campaign> targetCampaign = campaignRepository.findById(payload.getCampaignId());
        targetCampaign.ifPresent(campaignUserEvent::setCampaign);
        Optional<User> targetUser = userRepository.findById(payload.getUserId());
        targetUser.ifPresent(campaignUserEvent::setUser);
        campaignUserEvent.setUrl(payload.getUrl());

        if(campaignUserEvent.getCampaign() == null || campaignUserEvent.getUser() == null) {
            throw new RuntimeException("campaign or user is null campaignID: " + payload.getCampaignId() + " userID: " + payload.getUserId());
        }
        campaignUserEventRepository.save(campaignUserEvent);
    }

    public CampaignQueryResponseDTO campaignQuery(QueryDTO payload) {
        CampaignQueryResponseDTO res = new CampaignQueryResponseDTO();
        List<CampaignUserEvent> dbRes = campaignUserEventRepository.findByTimestampBetween(
                payload.getStartTime(), payload.getEndTime()
        );
        if(payload.getPlatform() != null && !payload.getPlatform().isEmpty()){
            dbRes = dbRes.stream().filter((CampaignUserEvent e) ->
                    e.getCampaign().getPlatform().equals(payload.getPlatform())).toList();
        }
        if(payload.getSubgroup() != null && !payload.getSubgroup().isEmpty()){
            dbRes = dbRes.stream().filter((CampaignUserEvent e) ->
                    e.getCampaign().getSubgroup().equals(payload.getSubgroup())).toList();
        }
        if(payload.getPostMemo() != null && !payload.getPostMemo().isEmpty()){
            dbRes = dbRes.stream().filter((CampaignUserEvent e) ->
                    e.getCampaign().getPostMemo().equals(payload.getPostMemo())).toList();
        }

        //return dbRes;
    }
}
