package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.context.UserContext;
import com.thelocalmusicfinder.localmusicfinderanalytics.models.User;
import com.thelocalmusicfinder.localmusicfinderanalytics.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;

    @Transactional
    public User upsertUser(UUID userId) {
      Optional<User> optionalUser = userRepository.findById(userId);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        user.setDeviceType(getNewColumnValue(user.getDeviceType(), userContext.getDeviceClass()));
        user.setUserAgent(getNewColumnValue(user.getUserAgent(), userContext.getUserAgent()));
        user.setIpAddress(getNewColumnValue(user.getIpAddress(), userContext.getIpAddress()));
        return user;
      }

      User user = new User();
      user.setId(userId);
      user.setDeviceType(userContext.getDeviceClass());
      user.setUserAgent(userContext.getUserAgent());
      user.setIpAddress(userContext.getIpAddress());
      return userRepository.save(user);
    }

    private String getNewColumnValue(String curValue, String newValue) {
      if (newValue == null) return curValue;
      if (curValue == null || curValue.contains(newValue)) return newValue;
      return curValue + "///" + newValue;
    }
}
