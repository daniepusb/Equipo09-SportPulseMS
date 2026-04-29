package com.sportpulse.ms_fixtures.service.impl;

import com.sportpulse.ms_fixtures.client.TeamsClient;
import com.sportpulse.ms_fixtures.dto.ApiFootballEventResponse;
import com.sportpulse.ms_fixtures.dto.FixtureEventResponse;
import com.sportpulse.ms_fixtures.dto.FixtureResponse;
import com.sportpulse.ms_fixtures.dto.TeamDto;
import com.sportpulse.ms_fixtures.mapper.FixtureEventMapper;
import com.sportpulse.ms_fixtures.mapper.FixtureMapper;
import com.sportpulse.ms_fixtures.model.Fixture;
import com.sportpulse.ms_fixtures.model.FixtureStatus;
import com.sportpulse.ms_fixtures.repository.FixtureRepository;
import com.sportpulse.ms_fixtures.service.FixtureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.sportpulse.ms_fixtures.exception.PlanRestrictionException;
import com.sportpulse.ms_fixtures.exception.FixtureNotFoundException;

@Service
@Slf4j
public class FixtureServiceImpl implements FixtureService {

    private final FixtureRepository fixtureRepository;
    private final FixtureMapper fixtureMapper;
    private final TeamsClient teamsClient;
    private final FixtureEventMapper fixtureEventMapper;

    public FixtureServiceImpl(FixtureRepository fixtureRepository, FixtureMapper fixtureMapper,
    		TeamsClient teamsClient, FixtureEventMapper fixtureEventMapper) {
        this.fixtureRepository = fixtureRepository;
        this.fixtureMapper = fixtureMapper;
        this.teamsClient = teamsClient;
        this.fixtureEventMapper = fixtureEventMapper;
    }

    @Override
    public List<FixtureResponse> getFixtures(Long league, Integer season, Long team, LocalDate date, FixtureStatus status) {
        boolean hasAnyFilter = league != null || team != null || date != null || status != null;
        LocalDate effectiveDate = hasAnyFilter ? date : LocalDate.now();

        Integer resolvedSeason = null;
        if (league != null) {
            resolvedSeason = (season != null) ? season : inferSeasonFromDate(effectiveDate);
            PlanRestrictionException.validate(resolvedSeason);
            log.info("Resolved season {} for league {} with date {}", resolvedSeason, league, effectiveDate);
        }

        List<Fixture> fixtures = fixtureRepository.findByFilters(league, resolvedSeason, team, effectiveDate, status);

        Set<Long> teamIds = fixtures.stream()
            .flatMap(f -> Stream.of(f.homeTeam().id(), f.awayTeam().id()))
            .collect(Collectors.toSet());

        Map<Long, TeamDto> teamsData = teamIds.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                id -> teamsClient.getTeamById(id)
            ));

        return fixtures.stream()
            .map(fixture -> fixtureMapper.toResponse(fixture, teamsData))
            .collect(Collectors.toList());
    }

    private Integer inferSeasonFromDate(LocalDate date) {
        LocalDate referenceDate = (date != null) ? date : LocalDate.now();
        int year = referenceDate.getYear();
        return referenceDate.getMonthValue() >= 8 ? year : year - 1;
    }

	@Override
	public List<FixtureEventResponse> getEventsByFixtureId(Integer fixtureId) {

		List<ApiFootballEventResponse> events = fixtureRepository.getEventsByFixtureId(fixtureId);

		if (events.isEmpty()) {
			throw new FixtureNotFoundException();
		}

		return events.stream()
	            .sorted(Comparator.comparing(e -> e.time().elapsed()))
	            .map(fixtureEventMapper::toResponse)
	            .collect(Collectors.toList());
	}
}