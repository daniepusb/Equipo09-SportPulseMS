package com.sportpulse.ms_teams.model;

import java.io.Serializable;

public record Stadium(
    String name,
    String address,
    String city,
    Integer capacity,
    String surface
) implements Serializable {}