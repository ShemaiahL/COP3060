package edu.famu.cop3060.resources.dto;

import java.util.List;

public record PageEnvelope<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
