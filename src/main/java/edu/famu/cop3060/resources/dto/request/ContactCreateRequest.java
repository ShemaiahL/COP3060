package edu.famu.cop3060.resources.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactCreateRequest(
        @NotBlank String fullName,
        @Email String email,
        String phone) {}
