package com.thelocalmusicfinder.localmusicfinderanalytics.services;

import com.thelocalmusicfinder.localmusicfinderanalytics.domain.LocationInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class LocationService {
  private final RestClient restClient;
  private final String adminKey;

  public LocationService(RestClient.Builder restClientBuilder,
                         @Value("${lmf.backend.base}") String lmfBackendBaseUrl,
                         @Value("${admin.key}") String adminKey) {
    this.restClient = restClientBuilder.baseUrl(lmfBackendBaseUrl).build();
    this.adminKey = adminKey;
  }

  public LocationInfo getLocationById(String locationId) {
    return this.restClient.get()
            .uri("/api/admin/location/" + locationId)
            .header("Admin-Key", adminKey)
            .retrieve()
            .body(LocationInfo.class);
  }
}
