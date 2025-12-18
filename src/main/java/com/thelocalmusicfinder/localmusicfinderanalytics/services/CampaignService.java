package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;

    @Transactional
    public Long findOrCreateCampaign(CreateCampaignDTO payload) {
      Optional<Campaign> foundCampaign = campaignRepository.findCampaignByPlatformAndSubgroupAndPostMemo(
              payload.getPlatform(), payload.getSubgroup(), payload.getPostMemo());
      if (foundCampaign.isPresent()) {
        return foundCampaign.get().getId();
      }

      Campaign campaign = new Campaign();
      campaign.setPlatform(payload.getPlatform());
      campaign.setSubgroup(payload.getSubgroup());
      campaign.setPostMemo(payload.getPostMemo());
      Campaign createdCampaign = campaignRepository.save(campaign);
      return createdCampaign.getId();
    }

    public List<CampaignDTO> getAllCampaigns() {
      List<Campaign> campaigns = campaignRepository.findAll();
      List<CampaignDTO> campaignDTOs = new ArrayList<>();
      for (Campaign campaign : campaigns) {
        CampaignDTO campaignDTO = new CampaignDTO(
                campaign.getPlatform(), campaign.getSubgroup(), campaign.getPostMemo());
        campaignDTOs.add(campaignDTO);
      }
      return campaignDTOs;
    }
}