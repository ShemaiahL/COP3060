package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;

public record UnitDTO(
        Long id,
        @NotBlank(message="name is required") String name,
        String abbreviation
) {}
