package com.thelocalmusicfinder.localmusicfinderanalytics.domain;

import java.util.UUID;

public record NameWithUserId(String name, UUID userId, boolean isNewSession, boolean isMobile) {}

