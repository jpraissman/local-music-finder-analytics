package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateBandUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateVenueUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateVideoUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.BandUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.VenueUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.VideoUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.BandUserService;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.UserService;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.VenueUserService;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.VideoUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final BandUserService bandUserService;
    private final VenueUserService venueUserService;
    private final VideoUserService videoUserService;
    private final UserService userService;
    //private final UserService userService;
    //private final VenueUserService venueUserService;

    /// POST METHODS
    @PostMapping("/band-user")
    public ResponseEntity<BandUserEvent> createBandUser(@Valid @RequestBody CreateBandUserDTO payload) {
        try {
            BandUserEvent event = bandUserService.createEvent(payload);
            return new ResponseEntity<>(event, HttpStatus.CREATED);
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            // service should throw a clear exception when user/band not found
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/venue-user")
    public VenueUserEvent createVenueUser(@Valid @RequestBody CreateVenueUserDTO payload) {
        return venueUserService.createEvent(payload);
    }

    @PostMapping("/video-user")
    public VideoUserEvent createVideoUser(@Valid @RequestBody CreateVideoUserDTO payload) {
        return videoUserService.createEvent(payload);
    }

    @PostMapping("/user")
    public User createUser(@Valid @RequestBody CreateUserDTO payload) {
        return userService.createUser(payload);
    }


    ///  GETTERS

    @GetMapping("/band-user")
    public ResponseEntity<List<BandUserEvent>>  fetchBandUserEvents(){
        List<BandUserEvent> bandUserEvents = bandUserService.getAllEvents();
        return new ResponseEntity<>(bandUserEvents, HttpStatus.ACCEPTED);
    }
    @GetMapping("/venue-user")
    public ResponseEntity<List<VenueUserEvent>>  fetchVenueUserEvents(){
        List<VenueUserEvent> venueUserEvents = venueUserService.getAllEvents();
        return new ResponseEntity<>(venueUserEvents, HttpStatus.ACCEPTED);
    }
    @GetMapping("/video-user")
    public ResponseEntity<List<VideoUserEvent>>  fetchVideoUserEvents(){
        List<VideoUserEvent> videoUserEvents = videoUserService.getAllEvents();
        return new ResponseEntity<>(videoUserEvents, HttpStatus.ACCEPTED);
    }
    @GetMapping("/user")
    public ResponseEntity<List<User>>  fetchUsers(){
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
    }
}
