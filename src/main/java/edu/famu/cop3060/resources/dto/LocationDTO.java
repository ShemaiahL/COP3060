package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationDTO(
        Long id,
        @NotBlank(message="building is required") String building,
        @NotBlank(message="room is required") String room,
        String notes
) {}
