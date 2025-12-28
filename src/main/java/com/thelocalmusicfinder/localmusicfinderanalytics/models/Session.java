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
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String urlEntry;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant sessionStart;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant lastSessionActivity;

    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String sessionActivityOverview;

    @Column()
    private int numScrolls;

    @Column()
    private Boolean isUsersFirstSession;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="campaign_id")
    private Campaign campaign;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Session session)) return false;
      return id.equals(session.id);
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
}
