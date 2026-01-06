package com.thelocalmusicfinder.localmusicfinderanalytics.services.event;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation.CreateVenueUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.VenueUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.VenueUserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueUserService {
    @Autowired
    private VenueUserRepository venueUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionService sessionService;

    public VenueUserEvent createEvent(CreateVenueUserDTO payload) {
        VenueUserEvent  venueUserEvent = new VenueUserEvent();
        Optional<User> user = userRepository.findById(payload.getUserId());
        if(user.isEmpty()){
            throw new IllegalArgumentException("user not present in user table");
        }
        Optional<Session> session = sessionService.getActiveSession(user.get().getId());
        if(session.isEmpty()){
            throw new IllegalArgumentException("no session found for user id" + payload.getUserId());
        }
        venueUserEvent.setUser(user.get());
        venueUserEvent.setVenueId(payload.getVenueId());
        venueUserEvent.setSession(session.get());

        return venueUserRepository.save(venueUserEvent);
    }
    public List<VenueUserEvent> getAllEvents() {
        return venueUserRepository.findAll();
    }
}
