package com.thelocalmusicfinder.localmusicfinderanalytics.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String platform;

    @Column()
    private String subgroup;

    @Column()
    private String postMemo;

    @CreationTimestamp
    @Column()
    private Instant createdAt;
}