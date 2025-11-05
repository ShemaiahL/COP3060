package edu.famu.cop3060.resources.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResourceUpdateRequest(@NotBlank String name,
                                    String description,
                                    @NotNull Long locationId,
                                    @NotNull Long unitId,
                                    @NotNull Long contactId) {}
