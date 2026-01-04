package com.thelocalmusicfinder.localmusicfinderanalytics.services.event;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.LocationInfo;
import com.thelocalmusicfinder.localmusicfinderanalytics.domain.NameWithSession;
import com.thelocalmusicfinder.localmusicfinderanalytics.domain.TotalNumbers;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation.CreateSearchUserEventDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.QueryDetail;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.SearchUserQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.SearchUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.SearchUserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.LocationService;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.SessionService;
import com.thelocalmusicfinder.localmusicfinderanalytics.util.QueryResponseUtils;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchUserService {
  private final SearchUserRepository searchUserRepository;
  private final UserRepository userRepository;
  private final LocationService locationService;
  private final SessionService sessionService;

  @Transactional
  public void createSearchUserEvent(CreateSearchUserEventDTO payload) {
    Optional<Session> optionalSession = sessionService.getActiveSession(payload.getUserId());
    if (optionalSession.isEmpty()) {
      return;
    }

    SearchUserEvent searchUserEvent = new SearchUserEvent();

    Optional<User> targetUser = userRepository.findById(payload.getUserId());
    targetUser.ifPresent(searchUserEvent::setUser);
    searchUserEvent.setSession(optionalSession.get());
    searchUserEvent.setCampaign(optionalSession.get().getCampaign());
    searchUserEvent.setLocationId(payload.getLocationId());
    searchUserEvent.setSearchContext(payload.getSearchContext());

    if(searchUserEvent.getUser() == null) {
      throw new RuntimeException("user is null. Given userID: " + payload.getUserId());
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
            query.getIncludeAdmin(), query.getPlatform(), query.getSubgroup(), query.getPostMemo());

    // filter events by min duration
    List<SearchUserEvent> filteredEvents = new ArrayList<>();
    for (SearchUserEvent event : events) {
      long durationInSec = Duration.between(event.getSession().getSessionStart(), event.getSession().getLastSessionActivity()).getSeconds();
      if (durationInSec > query.getMinDurationInSec()) {
        filteredEvents.add(event);
      }
    }

    List<QueryDetail> sublayerDetails = query.getPostMemo() == null ? this.getSublayerDetails(filteredEvents, query) : List.of();
    List<QueryDetail> counties = getQueryDetails(DetailType.COUNTY, filteredEvents);
    List<QueryDetail> towns = getQueryDetails(DetailType.TOWN, filteredEvents);
    List<QueryDetail> formattedAddresses = getQueryDetails(DetailType.ADDRESS, filteredEvents);
    List<QueryDetail> searchContexts = getQueryDetails(DetailType.SEARCH_CONTEXT, filteredEvents);

    TotalNumbers totalNumbers = getTotalNumbers(filteredEvents);

    return SearchUserQueryResponseDTO.builder()
            .sublayerDetails(sublayerDetails)
            .total(totalNumbers.total())
            .totalUnique(totalNumbers.totalUnique())
            .totalUniqueNew(totalNumbers.totalUniqueNew())
            .totalUniqueReturning(totalNumbers.totalUniqueReturning())
            .totalUniqueMobile(totalNumbers.totalUniqueMobile())
            .avgDurationInSec(totalNumbers.avgDurationInSec())
            .searchContexts(searchContexts)
            .formattedAddresses(formattedAddresses)
            .counties(counties)
            .towns(towns).build();
  }

  private TotalNumbers getTotalNumbers(List<SearchUserEvent> events) {
    List<Session> allSessions = new ArrayList<>();
    for (SearchUserEvent searchUserEvent : events) {
      allSessions.add(searchUserEvent.getSession());
    }
    return QueryResponseUtils.getTotalNumbers(allSessions);
  }

  private List<QueryDetail> getQueryDetails(DetailType type, List<SearchUserEvent> events) {
    List<NameWithSession> nameWithUserIds = new ArrayList<>();
    for (SearchUserEvent event : events) {
      String name = convertTypeToString(type, event);
      nameWithUserIds.add(new NameWithSession(name, event.getSession()));
    }
    return QueryResponseUtils.generateQueryDetailList(nameWithUserIds);
  }

  private List<QueryDetail> getSublayerDetails(List<SearchUserEvent> events, AnalyticsQueryDTO query) {
    List<NameWithSession> sublayerNamesWithUserIds = new ArrayList<>();
    for (SearchUserEvent event : events) {
      String sublayerName = QueryResponseUtils.getSublayerName(event.getCampaign(), query);
      sublayerNamesWithUserIds.add(new NameWithSession(sublayerName, event.getSession()));
    }
    return QueryResponseUtils.generateQueryDetailList(sublayerNamesWithUserIds);
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
