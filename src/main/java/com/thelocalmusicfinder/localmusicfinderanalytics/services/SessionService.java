package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.NameWithSession;
import com.thelocalmusicfinder.localmusicfinderanalytics.domain.TotalNumbers;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.AnalyticsQueryDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.QueryDetail;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.query.SessionQueryResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.session.BasicSessionDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.session.SessionHeartbeatDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.session.StartSessionDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.campaign.CreateCampaignDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Campaign;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.CampaignRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.SessionRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.util.QueryResponseUtils;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {
  private final CampaignRepository campaignRepository;
  private final SessionRepository sessionRepository;
  private final UserService userService;
  private final CampaignService campaignService;

  @Transactional
  public void startSession(StartSessionDTO payload) {
    User user = userService.upsertUser(payload.getUserId());

    Optional<Session> optionalSession = getActiveSession(payload.getUserId());
    if (optionalSession.isEmpty()) {
      boolean isUsersFirstSession = sessionRepository.findByUserId(payload.getUserId()).isEmpty();

      Session newSession = new Session();
      Campaign sessionCampaign = getSessionCampaign(payload.getCampaignId(), payload.getReferer());
      newSession.setUser(user);
      newSession.setUrlEntry(payload.getUrlEntry());
      newSession.setSessionActivityOverview("User entered the site from " + sessionCampaign.getPlatform() + " and went to the path " + payload.getUrlEntry() + "\n");
      newSession.setCampaign(sessionCampaign);
      newSession.setIsUsersFirstSession(isUsersFirstSession);
      sessionRepository.save(newSession);
    } else {
      Session activeSession = optionalSession.get();
      if (activeSession.getCampaign().getPlatform().equals("Unknown")) {
        Campaign newCampaign = this.getSessionCampaign(payload.getCampaignId(), payload.getReferer());
        if (!newCampaign.getPlatform().equals("Unknown")) {
          activeSession.setCampaign(newCampaign);
          activeSession.setUrlEntry(payload.getUrlEntry());
          String curActivityOverview = activeSession.getSessionActivityOverview();
          activeSession.setSessionActivityOverview(curActivityOverview + "\nUser entered the site from " + newCampaign.getPlatform() + " and went to the path " + payload.getUrlEntry() + "\n");
        }
      }
    }
  }

  private Campaign getSessionCampaign(Long campaignId, String referer) {
    if (campaignId != null) {
      Optional<Campaign> optionalCampaign = campaignRepository.findById(campaignId);
      if (optionalCampaign.isEmpty()) {
        throw new RuntimeException("Campaign with id " + campaignId + " not found");
      }
      return optionalCampaign.get();
    }

    String refererToUse = referer == null ? "Unknown" : referer;
    CreateCampaignDTO refererCampaign = new CreateCampaignDTO(refererToUse, refererToUse, refererToUse);
    return campaignService.findOrCreateCampaign(refererCampaign);
  }

  @Transactional
  public void heartbeat(SessionHeartbeatDTO payload) {
    Optional<Session> optionalSession = getActiveSession(payload.getUserId());
    if (optionalSession.isPresent()) {
      Session activeSession = optionalSession.get();
      activeSession.setLastSessionActivity(Instant.now());
      if (!payload.getActivityOverview().trim().isEmpty()) {
        String curActivityOverview = activeSession.getSessionActivityOverview();
        activeSession.setSessionActivityOverview(curActivityOverview + payload.getActivityOverview() + "\n");
      }
      activeSession.setNumScrolls(activeSession.getNumScrolls() + payload.getNumScrolls());
    }
  }

  public Optional<Session> getActiveSession(UUID userId) {
    Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);
    return sessionRepository.findFirstByUserIdAndLastSessionActivityAfterOrderByLastSessionActivityDesc(userId, tenMinutesAgo);
  }

  public String getSessionLogs(Long sessionId) {
    Optional<Session> optionalSession = sessionRepository.findById(sessionId);
    if (optionalSession.isEmpty()) {
      return "Unable to find logs";
    }
    return optionalSession.get().getSessionActivityOverview();
  }

  public SessionQueryResponseDTO query(AnalyticsQueryDTO query) {
    Instant startInstant = QueryResponseUtils.getStartInstant(query);
    Instant endInstant = QueryResponseUtils.getEndInstant(query);
    List<Session> sessions = sessionRepository.findSessions(startInstant, endInstant,
            query.getIncludeAdmin(), query.getPlatform(), query.getSubgroup(), query.getPostMemo());

    // filter session by min duration
    List<Session> filteredSessions = new ArrayList<>();
    for (Session session : sessions) {
      long durationInSec = Duration.between(session.getSessionStart(), session.getLastSessionActivity()).getSeconds();
      if (durationInSec > query.getMinDurationInSec()) {
        filteredSessions.add(session);
      }
    }

    List<QueryDetail> sublayerDetails = query.getPostMemo() == null ? this.getSublayerDetails(filteredSessions, query) : List.of();
    List<QueryDetail> pathDetails = this.getPathDetails(filteredSessions);
    List<BasicSessionDTO> sessionDTOs = this.getSessionDetails(filteredSessions);

    TotalNumbers totalNumbers = QueryResponseUtils.getTotalNumbers(filteredSessions);

    return SessionQueryResponseDTO.builder()
            .total(totalNumbers.total())
            .totalUnique(totalNumbers.totalUnique())
            .totalUniqueNew(totalNumbers.totalUniqueNew())
            .totalUniqueReturning(totalNumbers.totalUniqueReturning())
            .totalUniqueMobile(totalNumbers.totalUniqueMobile())
            .avgDurationInSec(totalNumbers.avgDurationInSec())
            .sublayerDetails(sublayerDetails)
            .pathDetails(pathDetails)
            .sessions(sessionDTOs).build();
  }

  private List<QueryDetail> getPathDetails(List<Session> sessions) {
    List<NameWithSession> pathNamesWithUserIds = new ArrayList<>();
    for (Session session : sessions) {
      pathNamesWithUserIds.add(new NameWithSession(session.getUrlEntry(), session));
    }
    return QueryResponseUtils.generateQueryDetailList(pathNamesWithUserIds);
  }

  private List<BasicSessionDTO> getSessionDetails(List<Session> sessions) {
    List<BasicSessionDTO> sessionDTOs = new ArrayList<>();
    for (Session session : sessions) {
      BasicSessionDTO sessionDTO = BasicSessionDTO.builder()
              .sessionId(session.getId())
              .userId(session.getUser().getId())
              .durationInSec((int) Duration.between(session.getSessionStart(), session.getLastSessionActivity()).getSeconds())
              .platform(session.getCampaign().getPlatform())
              .urlEntry(session.getUrlEntry())
              .isNewSession(session.getIsUsersFirstSession())
              .ipAddress(session.getUser().getIpAddress())
              .numScrolls(session.getNumScrolls()).build();
      sessionDTOs.add(sessionDTO);
    }
    return sessionDTOs;
  }

  private List<QueryDetail> getSublayerDetails(List<Session> sessions, AnalyticsQueryDTO query) {
    List<NameWithSession> sublayerNamesWithUserIds = new ArrayList<>();
    for (Session session : sessions) {
      String sublayerName = QueryResponseUtils.getSublayerName(session.getCampaign(), query);
      sublayerNamesWithUserIds.add(new NameWithSession(sublayerName, session));
    }
    return QueryResponseUtils.generateQueryDetailList(sublayerNamesWithUserIds);
  }
}
