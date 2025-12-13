package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;

    @Transactional
    public Long createCampaign(CreateCampaignDTO payload) {
        Campaign campaign = new Campaign();
        campaign.setPlatform(payload.getPlatform());
        campaign.setSubgroup(payload.getSubgroup());
        campaign.setPostMemo(payload.getPostMemo());
        Campaign createdCampaign = campaignRepository.save(campaign);
        return createdCampaign.getId();
    }
}
