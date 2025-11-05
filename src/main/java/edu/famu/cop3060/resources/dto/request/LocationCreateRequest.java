package edu.famu.cop3060.resources.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LocationCreateRequest(
        @NotBlank String building,
        @NotBlank String room,
        String notes
) {}
