package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactDTO(
        Long id,
        @NotBlank(message="fullName is required") String fullName,
        @Email(message="email must be valid") String email,
        String phone
) {}
