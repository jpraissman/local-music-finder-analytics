package com.thelocalmusicfinder.localmusicfinderanalytics.controllers;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.user.CreateUserResponseDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
//    private final BandUserService bandUserService;
//    private final VenueUserService venueUserService;
//    private final VideoUserService videoUserService;
    private final UserService userService;

    /// POST METHODS
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

    @PostMapping(
            value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CreateUserResponseDTO> createUser() {
        User createdUser = userService.createUser();
        CreateUserResponseDTO response = new CreateUserResponseDTO(createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
