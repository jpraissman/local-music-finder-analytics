package com.thelocalmusicfinder.localmusicfinderanalytics.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="generated_key")
    private UUID key = UUID.randomUUID();

    @Column(name="platform")
    private String platform;

    @Column(name="subgroup")
    private String subgroup;

    /**
     * Link of the social media post this is going to
     */
    @Column(name="post_url",unique = true)
    private String postUrl;

    /**
     * The actual link we want people to be going to
     */
    @Column(name="target_url")
    private String targetUrl;

    @CreationTimestamp
    @Column(name="created_at")
    private Instant createdAt;
}