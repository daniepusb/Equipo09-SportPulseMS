package com.sportpulse.ms_fixtures.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiFootballEventResponse(
 @JsonProperty("time") Time time,
 @JsonProperty("team") Team team,
 @JsonProperty("player") Player player,
 @JsonProperty("assist") Assist assist,
 @JsonProperty("type") String type,
 @JsonProperty("detail") String detail
) {
 public record Time(@JsonProperty("elapsed") Integer elapsed) {}
 public record Team(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("logo") String logo) {}
 public record Player(@JsonProperty("id") Long id, @JsonProperty("name") String name) {}
 public record Assist(@JsonProperty("id") Long id, @JsonProperty("name") String name) {}
}