package com.sportpulse.ms_fixtures.exception;

public class FixtureNotFoundException extends RuntimeException {
	public FixtureNotFoundException() {
		super("FIXTURE_NOT_FOUND");
	}

}
