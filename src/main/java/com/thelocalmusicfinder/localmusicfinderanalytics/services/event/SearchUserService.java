package com.thelocalmusicfinder.localmusicfinderanalytics.services.event;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.LocationInfo;
import com.thelocalmusicfinder.localmusicfinderanalytics.domain.NameWithUserId;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation.CreateSearchUserEventDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.QueryDetail;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.queryresponse.searchuser.SearchUserQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.SearchUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.SearchUserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.LocationService;
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
public class SearchUserService {
  private final SearchUserRepository searchUserRepository;
  private final CampaignRepository campaignRepository;
  private final UserRepository userRepository;
  private final LocationService locationService;

  @Transactional
  public void createSearchUserEvent(CreateSearchUserEventDTO payload) {
    SearchUserEvent searchUserEvent = new SearchUserEvent();

    Optional<Campaign> targetCampaign = campaignRepository.findById(payload.getCampaignId());
    targetCampaign.ifPresent(searchUserEvent::setCampaign);
    Optional<User> targetUser = userRepository.findById(payload.getUserId());
    targetUser.ifPresent(searchUserEvent::setUser);
    searchUserEvent.setLocationId(payload.getLocationId());
    searchUserEvent.setSearchContext(payload.getSearchContext());

    if(searchUserEvent.getCampaign() == null || searchUserEvent.getUser() == null) {
      throw new RuntimeException("campaign or user is null campaignID: " + payload.getCampaignId() + " userID: " + payload.getUserId());
    }

    LocationInfo locationInfo = locationService.getLocationById(payload.getLocationId());
    searchUserEvent.setFormattedAddress(locationInfo.getFormattedAddress());
    searchUserEvent.setTown(locationInfo.getTown());
    searchUserEvent.setCounty(locationInfo.getCounty());

    searchUserRepository.save(searchUserEvent);
  }

  public SearchUserQueryResponseDTO query(AnalyticsQueryDTO query) {
    Instant startInstant = QueryResponseUtils.getStartInstant(query);
    Instant endInstant = QueryResponseUtils.getEndInstant(query);
    List<SearchUserEvent> events = searchUserRepository.findEvents(startInstant, endInstant,
            query.getPlatform(), query.getSubgroup(), query.getPostMemo());

    List<QueryDetail> counties = getQueryDetails(DetailType.COUNTY, events);
    List<QueryDetail> towns = getQueryDetails(DetailType.TOWN, events);
    List<QueryDetail> formattedAddresses = getQueryDetails(DetailType.ADDRESS, events);
    List<QueryDetail> searchContexts = getQueryDetails(DetailType.SEARCH_CONTEXT, events);

    int totalSearches = events.size();
    int uniqueUsersWhoSearched = getUniqueUsersWhoSearched(events);

    return new SearchUserQueryResponseDTO(totalSearches, uniqueUsersWhoSearched, counties, towns, formattedAddresses, searchContexts);
  }

  private int getUniqueUsersWhoSearched(List<SearchUserEvent> events) {
    List<UUID> allUserIds = new ArrayList<>();
    for (SearchUserEvent searchUserEvent : events) {
      allUserIds.add(searchUserEvent.getUser().getId());
    }
    return QueryResponseUtils.getTotalUnique(allUserIds);
  }

  private List<QueryDetail> getQueryDetails(DetailType type, List<SearchUserEvent> events) {
    List<NameWithUserId> nameWithUserIds = new ArrayList<>();
    for (SearchUserEvent event : events) {
      String name = convertTypeToString(type, event);
      nameWithUserIds.add(new NameWithUserId(name, event.getUser().getId()));
    }
    return QueryResponseUtils.generateQueryDetailList(nameWithUserIds);
  }

  private String convertTypeToString(DetailType type, SearchUserEvent searchUserEvent) {
    String result = switch (type) {
      case ADDRESS -> searchUserEvent.getFormattedAddress();
      case TOWN -> searchUserEvent.getTown();
      case COUNTY -> searchUserEvent.getCounty();
      case SEARCH_CONTEXT -> searchUserEvent.getSearchContext();
    };
    return result == null ? "Unknown" : result;
  }
}

enum DetailType {
  COUNTY,
  TOWN,
  ADDRESS,
  SEARCH_CONTEXT
}
