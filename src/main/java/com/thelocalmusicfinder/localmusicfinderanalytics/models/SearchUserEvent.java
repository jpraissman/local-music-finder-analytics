package com.thelocalmusicfinder.localmusicfinderanalytics.models;

import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column()
  private String locationId;

  @Column()
  private String formattedAddress;

  @Column()
  private String town;

  @Column()
  private String county;

  @Column()
  private String searchContext;

  @CreationTimestamp
  @Column(name="timestamp", updatable = false, nullable = false)
  private Instant timestamp;

  @ManyToOne(fetch= FetchType.LAZY)
  @JoinColumn(name="user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="campaign_id")
  private Campaign campaign;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="session_id")
  private Session session;
}
