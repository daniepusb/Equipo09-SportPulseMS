package com.sportpulse.ms_fixtures.exception;

public class PlanRestrictionException extends RuntimeException {
    private static final int MIN_FREE_SEASON = 2022;
    private static final int MAX_FREE_SEASON = 2024;

    public PlanRestrictionException(Integer season) {
        super("Season %d is not available on the free plan. Valid range: %d–%d."
            .formatted(season, MIN_FREE_SEASON, MAX_FREE_SEASON));
    }

    public static void validate(Integer season) {
        if (season < MIN_FREE_SEASON || season > MAX_FREE_SEASON) {
            throw new PlanRestrictionException(season);
        }
    }
}