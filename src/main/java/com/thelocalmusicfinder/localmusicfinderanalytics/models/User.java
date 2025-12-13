package com.thelocalmusicfinder.localmusicfinderanalytics.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"bandUserEvents", "venueUserEvents","videoUserEvents"})
@Table(name ="users")
public class User {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(name="location", nullable=false)
    private String location;

    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @Column(name="referrer")
    private String referrer;

    @Column(name="operating_system")
    private String operatingSystem;

    @Column(name="browser")
    private String browser;

    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL)
    private List<VenueUserEvent> venueUserEvents;
    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL)
    private List<VideoUserEvent> videoUserEvents;
    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL)
    private List<BandUserEvent> bandUserEvents;
}

