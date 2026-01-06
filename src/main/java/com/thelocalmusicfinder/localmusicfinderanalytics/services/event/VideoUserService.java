package com.thelocalmusicfinder.localmusicfinderanalytics.services.event;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.eventcreation.CreateVideoUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.VideoUserEvent;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.VideoUserRepository;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoUserService {
    @Autowired
    private VideoUserRepository videoUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionService sessionService;

    public VideoUserEvent createEvent(CreateVideoUserDTO payload) {
        VideoUserEvent  videoUserEvent = new VideoUserEvent();
        Optional<User> user = userRepository.findById(payload.getUserId());
        if(user.isEmpty()){
            throw new IllegalArgumentException("user id " + payload.getUserId() + " not present in user table");
        }
        Optional<Session> session = sessionService.getActiveSession(user.get().getId());
        if(session.isEmpty()){
            throw new IllegalArgumentException("no session found for user id" + payload.getUserId());
        }
        videoUserEvent.setUser(user.get());
        videoUserEvent.setVideoId(payload.getVideoId());
        videoUserEvent.setSession(session.get());

        return videoUserRepository.save(videoUserEvent);
    }

    public List<VideoUserEvent> getAllEvents(){
        return videoUserRepository.findAll();
    }

}
