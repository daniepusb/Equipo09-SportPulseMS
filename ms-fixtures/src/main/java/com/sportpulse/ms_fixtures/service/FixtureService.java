package com.sportpulse.ms_fixtures.service;

import java.time.LocalDate;
import java.util.List;
import com.sportpulse.ms_fixtures.dto.FixtureResponse;
import com.sportpulse.ms_fixtures.model.FixtureStatus;

public interface FixtureService {
    List<FixtureResponse> getFixtures(Long league, Long team, LocalDate date, FixtureStatus status);
}