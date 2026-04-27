package com.sportpulse.ms_fixtures.repository;

import java.time.LocalDate;
import java.util.List;
import com.sportpulse.ms_fixtures.model.Fixture;
import com.sportpulse.ms_fixtures.model.FixtureStatus;

public interface FixtureRepository {
    List<Fixture> findByFilters(Long league, Integer season, Long team, LocalDate date, FixtureStatus status);
}
