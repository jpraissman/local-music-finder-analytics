package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateCampaignUserEventDTO;
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

        campaignUserEventRepository.save(campaignUserEvent);
    }

    public List<CampaignUserEvent> getAllEvents(){
        return campaignUserEventRepository.findAll();
    }
    public List<Campaign> getAllCampaigns(){
        return campaignRepository.findAll();
    }
}
