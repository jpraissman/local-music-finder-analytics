package com.thelocalmusicfinder.localmusicfinderanalytics.services.event;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.NameWithUserId;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation.CreateCampaignUserEventDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.QueryDetail;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.campaign.CampaignUserQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.CampaignUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignUserEventRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.util.QueryResponseUtils;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignUserService {
  private final CampaignRepository campaignRepository;
  private final UserRepository userRepository;
  private final CampaignUserEventRepository campaignUserEventRepository;

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

  public CampaignUserQueryResponseDTO query(AnalyticsQueryDTO query) {
    Instant startInstant = QueryResponseUtils.getStartInstant(query);
    Instant endInstant = QueryResponseUtils.getEndInstant(query);
    List<CampaignUserEvent> events = campaignUserEventRepository.findEvents(startInstant, endInstant,
            query.getPlatform(), query.getSubgroup(), query.getPostMemo());

    List<QueryDetail> sublayerDetails = query.getPostMemo() == null ? this.getSublayerDetails(events, query) : List.of();
    List<QueryDetail> pathDetails = this.getPathDetails(events);

    int totalUsers = events.size();
    int totalUniqueUsers = getTotalUniqueUsers(events);

    return new CampaignUserQueryResponseDTO(totalUsers, totalUniqueUsers, sublayerDetails, pathDetails);
  }

  private int getTotalUniqueUsers(List<CampaignUserEvent> events) {
    List<UUID> allUserIds = new ArrayList<>();
    for (CampaignUserEvent campaignUserEvent : events) {
      allUserIds.add(campaignUserEvent.getUser().getId());
    }
    return QueryResponseUtils.getTotalUnique(allUserIds);
  }

  private List<QueryDetail> getPathDetails(List<CampaignUserEvent> events) {
    List<NameWithUserId> pathNamesWithUserIds = new ArrayList<>();
    for (CampaignUserEvent event : events) {
      pathNamesWithUserIds.add(new NameWithUserId(event.getUrl(), event.getUser().getId()));
    }
    return QueryResponseUtils.generateQueryDetailList(pathNamesWithUserIds);
  }

  private List<QueryDetail> getSublayerDetails(List<CampaignUserEvent> events, AnalyticsQueryDTO query) {
    List<NameWithUserId> sublayerNamesWithUserIds = new ArrayList<>();
    for (CampaignUserEvent event : events) {
      String sublayerName = this.getSublayerName(event, query);
      sublayerNamesWithUserIds.add(new NameWithUserId(sublayerName, event.getUser().getId()));
    }
    return QueryResponseUtils.generateQueryDetailList(sublayerNamesWithUserIds);
  }

  private String getSublayerName(CampaignUserEvent event, AnalyticsQueryDTO query) {
    if (query.getPlatform() == null) {
      return event.getCampaign().getPlatform();
    }
    if (query.getSubgroup() == null) {
      return event.getCampaign().getSubgroup();
    }
    if (query.getPostMemo() == null) {
      return event.getCampaign().getPostMemo();
    }
    return null;
  }
}
