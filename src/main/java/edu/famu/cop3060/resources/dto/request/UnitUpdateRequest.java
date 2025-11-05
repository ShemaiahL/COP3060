package edu.famu.cop3060.resources.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UnitUpdateRequest(
        @NotBlank String name,
        String abbreviation) {}
