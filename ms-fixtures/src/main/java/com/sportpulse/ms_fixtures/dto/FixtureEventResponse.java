package com.sportpulse.ms_fixtures.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FixtureEventResponse(
		@JsonProperty("elapsed") Integer elapsed,
		@JsonProperty("type") String type,
		@JsonProperty("detail") String detail,
		@JsonProperty("team") TeamInfo team,
		@JsonProperty("player") PlayerInfo player,
		@JsonProperty("assist") AssistInfo assist
		) {
	public record TeamInfo (
			@JsonProperty("id") Long id, 
			@JsonProperty("name") String name,
			@JsonProperty("logo") String logo
	) {}
	
	public record PlayerInfo (
			@JsonProperty("id") Long id, 
			@JsonProperty("name") String name
	) {}

	public record AssistInfo (
			@JsonProperty("id") Long id, 
			@JsonProperty("name") String name
	) {}
}
