package com.thelocalmusicfinder.localmusicfinderanalytics.services.event;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation.CreateBandUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.BandUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.BandUserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandUserService {

    @Autowired
    private final BandUserRepository bandUserRepository;
    @Autowired
    private final UserRepository userRepository;

    //private final BandUserRepository bandUserRepository;
    public BandUserEvent createEvent(CreateBandUserDTO payload) {
        Optional<User> user = userRepository.findById(payload.getUserId());
        if(user.isEmpty()){
            throw new IllegalArgumentException("user id " + payload.getUserId() + " not present in user table");
        }
        BandUserEvent bandUserEvent = new BandUserEvent();
        bandUserEvent.setUser(user.get());
        bandUserEvent.setBandId(payload.getBandId());

        return bandUserRepository.save(bandUserEvent);
    }

    public List<BandUserEvent> getAllEvents() {
        return bandUserRepository.findAll();
    }
}
