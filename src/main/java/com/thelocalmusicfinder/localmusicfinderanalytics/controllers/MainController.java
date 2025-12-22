package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.session.SessionHeartbeatDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.session.StartSessionDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation.CreateSearchUserEventDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.SessionService;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.event.SearchUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {
  private final SearchUserService  searchUserService;
  private final SessionService sessionService;

  @PostMapping("/session")
  public ResponseEntity<Void> startSession(@Valid @RequestBody StartSessionDTO payload) {
    sessionService.startSession(payload);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/session/heartbeat")
  public ResponseEntity<Void> heartbeat(@Valid @RequestBody SessionHeartbeatDTO payload) {
    sessionService.heartbeat(payload);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/session/search-user")
  public ResponseEntity<Void> createSearchUserEvent(@Valid @RequestBody CreateSearchUserEventDTO payload) {
    searchUserService.createSearchUserEvent(payload);
    return ResponseEntity.ok().build();
  }

    // POST METHODS
//    @PostMapping("/band-user")
//    public ResponseEntity<BandUserEvent> createBandUser(@Valid @RequestBody CreateBandUserDTO payload) {
//        try {
//            BandUserEvent event = bandUserService.createEvent(payload);
//            return new ResponseEntity<>(event, HttpStatus.CREATED);
//        } catch (IllegalArgumentException | EntityNotFoundException ex) {
//            // service should throw a clear exception when user/band not found
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
//        } catch (Exception ex) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//        }
//    }

//    @PostMapping("/venue-user")
//    public VenueUserEvent createVenueUser(@Valid @RequestBody CreateVenueUserDTO payload) {
//        return venueUserService.createEvent(payload);
//    }
//
//    @PostMapping("/video-user")
//    public VideoUserEvent createVideoUser(@Valid @RequestBody CreateVideoUserDTO payload) {
//        return videoUserService.createEvent(payload);
//    }
}
