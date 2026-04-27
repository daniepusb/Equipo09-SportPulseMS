package com.sportpulse.ms_fixtures.service;

import com.sportpulse.ms_fixtures.dto.FixtureResponse;
import com.sportpulse.ms_fixtures.mapper.FixtureMapper;
import com.sportpulse.ms_fixtures.model.FixtureStatus;
import com.sportpulse.ms_fixtures.repository.FixtureRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FixtureServiceImpl implements FixtureService {

    private final FixtureRepository fixtureRepository;
    private final FixtureMapper fixtureMapper;
    private final TeamsClient teamsClient;

    public FixtureServiceImpl(FixtureRepository fixtureRepository, FixtureMapper fixtureMapper, TeamsClient teamsClient) {
        this.fixtureRepository = fixtureRepository;
        this.fixtureMapper = fixtureMapper;
        this.teamsClient = teamsClient;
    }

    public List<FixtureResponse> getFixtures(Long league, Long team, LocalDate date, FixtureStatus status) {
        boolean hasAnyFilter = league != null || team != null || date != null || status != null;
        LocalDate effectiveDate = hasAnyFilter ? date : LocalDate.now();
        
        Integer season = null;
        if (league != null) {
            season = inferSeasonFromDate(effectiveDate);
            log.info("Infered season {} for league {} with date {}", season, league, effectiveDate);
        }
        
        List<Fixture> fixtures = fixtureRepository.findByFilters(league, season, team, effectiveDate, status).stream()
            .map(fixtureMapper::toResponse)
            .collect(Collectors.toList());

        Set<Long> teamIds = fixtures.stream()
            .flatMap(f -> Stream.of(f.homeTeam().id(), f.awayTeam().id()))
            .collect(Collectors.toSet());
        
        Map<Long, TeamDto> teamsData = teamsClient.getTeamsByIds(new ArrayList<>(teamIds))
            .stream()
            .collect(Collectors.toMap(TeamDto::id, Function.identity()));
        
        return fixtures.stream()
            .map(fixture -> fixtureMapper.toResponse(fixture, teamsData))
            .collect(Collectors.toList());
    }

    private Integer inferSeasonFromDate(LocalDate date) {
        LocalDate referenceDate = (date != null) ? date : LocalDate.now();
        int year = referenceDate.getYear();
        return referenceDate.getMonthValue() >= 8 ? year : year - 1;
    }
}