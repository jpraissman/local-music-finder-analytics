package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.dto.CreateUserDTO;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Create user function
     * @param payload we take location, os, browser, and referrer and mark it as a user
     * @return the saved user, including id
     */
    public User createUser(CreateUserDTO payload) {
        User u = new User();
        u.setLocation(payload.getLocation());
        u.setOperatingSystem(payload.getOperatingSystem());
        u.setBrowser(payload.getBrowser());
        u.setReferrer(payload.getReferrer());

        User savedUser = userRepository.save(u);
        System.out.println(savedUser);

        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
