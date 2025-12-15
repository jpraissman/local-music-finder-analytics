package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.queryresponse.CampaignQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateCampaignUserEventDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CampaignUserQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.queryresponse.QueryDetail;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;

import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignUserEventRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final CampaignUserEventRepository campaignUserEventRepository;

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

    public CampaignQueryResponseDTO campaignQuery(CampaignUserQueryDTO payload) {
      // Start of first day (inclusive) and Start of day AFTER endDate (exclusive)
      ZoneId eastern = ZoneId.of("America/New_York");
      Instant startInstant = payload.getStartDate().atStartOfDay(eastern).toInstant();
      Instant endInstant = payload.getEndDate().plusDays(1).atStartOfDay(eastern).toInstant();

      List<CampaignUserEvent> events = campaignUserEventRepository.findEvents(startInstant, endInstant,
              payload.getPlatform(), payload.getSubgroup(), payload.getPostMemo());

      List<QueryDetail> sublayerDetails = payload.getPostMemo() == null ? this.getSublayerDetails(events, payload) : List.of();
      List<QueryDetail> pathDetails = this.getPathDetails(events, payload);
      return new CampaignQueryResponseDTO(sublayerDetails, pathDetails);
    }

    private List<QueryDetail> getPathDetails(List<CampaignUserEvent> events, CampaignUserQueryDTO payload) {
      Map<String, QueryDetailWithUniqueUsers> pathDetails = new HashMap<>();
      for (CampaignUserEvent event : events) {
        String path = event.getUrl();
        updateQueryDetails(pathDetails, event, path);
      }
      return this.formatQueryDetailResponse(pathDetails);
    }

    private List<QueryDetail> getSublayerDetails(List<CampaignUserEvent> events, CampaignUserQueryDTO payload) {
      Map<String, QueryDetailWithUniqueUsers> sublayerDetails = new HashMap<>();
      for (CampaignUserEvent event : events) {
        String sublayerName = this.getSublayerName(event, payload);
        updateQueryDetails(sublayerDetails, event, sublayerName);
      }
      return this.formatQueryDetailResponse(sublayerDetails);
    }

    private List<QueryDetail> formatQueryDetailResponse(Map<String, QueryDetailWithUniqueUsers> queryDetails) {
      List<QueryDetail> result = new ArrayList<>();
      for (QueryDetailWithUniqueUsers queryDetailWithUniqueUsers : queryDetails.values()) {
        result.add(queryDetailWithUniqueUsers.queryDetail());
      }
      return result;
    }

    private void updateQueryDetails(Map<String, QueryDetailWithUniqueUsers> queryDetails, CampaignUserEvent event, String key) {
      if (queryDetails.containsKey(key)) {
        QueryDetailWithUniqueUsers pathDetail = queryDetails.get(key);
        QueryDetail queryDetail = pathDetail.queryDetail();
        queryDetail.setTotalUsers(queryDetail.getTotalUsers() + 1);

        Set<UUID> uniqueUsers = pathDetail.uniqueUsers();
        if (!uniqueUsers.contains(event.getUser().getId())) {
          uniqueUsers.add(event.getUser().getId());
          queryDetail.setTotalUniqueUsers(queryDetail.getTotalUniqueUsers() + 1);
        }
      } else {
        QueryDetail newQueryDetails = new QueryDetail(key, 1, 1);
        Set<UUID> uniqueUsers = new HashSet<>();
        uniqueUsers.add(event.getUser().getId());
        queryDetails.put(key, new QueryDetailWithUniqueUsers(newQueryDetails, uniqueUsers));
      }
    }

    private String getSublayerName(CampaignUserEvent event, CampaignUserQueryDTO payload) {
      if (payload.getPlatform() == null) {
        return event.getCampaign().getPlatform();
      }
      if (payload.getSubgroup() == null) {
        return event.getCampaign().getSubgroup();
      }
      if (payload.getPostMemo() == null) {
        return event.getCampaign().getPostMemo();
      }
      return null;
    }
}

record QueryDetailWithUniqueUsers(QueryDetail queryDetail, Set<UUID> uniqueUsers) {}
