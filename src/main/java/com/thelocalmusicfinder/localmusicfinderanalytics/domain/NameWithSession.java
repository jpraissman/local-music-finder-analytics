package com.thelocalmusicfinder.localmusicfinderanalytics.domain;

import com.thelocalmusicfinder.localmusicfinderanalytics.models.Session;

public record NameWithSession(String name, Session session) {}

