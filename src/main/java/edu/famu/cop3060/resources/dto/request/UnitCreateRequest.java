package edu.famu.cop3060.resources.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UnitCreateRequest(
        @NotBlank String name,
        String abbreviation
) {}
