package com.thelocalmusicfinder.localmusicfinderanalytics.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lmf_user")
public class User {
    @Id
    private UUID id;

    @Column()
    private String userAgent;

    @Column()
    private String deviceType;

    @Column()
    private String ipAddress;

    @Column(nullable = false)
    private boolean isAdmin;

    @CreationTimestamp
    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL)
    private List<Session> sessions;
}

