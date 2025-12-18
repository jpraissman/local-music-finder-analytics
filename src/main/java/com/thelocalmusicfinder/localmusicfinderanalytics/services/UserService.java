package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.context.UserContext;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;

    /**
     * Create user function
     * @return the saved user, including id
     */
    @Transactional
    public User createUser() {
      User u = new User();
      u.setDeviceType(userContext.getDeviceClass());
      u.setUserAgent(userContext.getUserAgent());
      u.setIpAddress(userContext.getIpAddress());
      return userRepository.save(u);
    }

    @Transactional
    public void updateUserUsingContext(UUID userId) {
      Optional<User> user = userRepository.findById(userId);
      if (user.isPresent()) {
        User u = user.get();
        String curIpAddresses = u.getIpAddress();
        String newIpAddress = userContext.getIpAddress();
        if (curIpAddresses == null) {
          u.setIpAddress(userContext.getIpAddress());
        }
        else if (newIpAddress != null) {
          if (!curIpAddresses.contains(newIpAddress)) {
            u.setIpAddress(curIpAddresses + "///" + newIpAddress);
          }
        }
      }
    }
}
