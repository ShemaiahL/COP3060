package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResourceDTO(
        Long id,
        @NotBlank(message = "name is required") String name,
        String description,
        @NotNull(message="locationId is required") Long locationId,
        @NotNull(message="unitId is required") Long unitId,
        @NotNull(message="contactId is required") Long contactId
) {}
