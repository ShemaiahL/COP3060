package edu.famu.cop3060.resources.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(OffsetDateTime timestamp, int status, String error,
                       String message, String path, Map<String,String> fieldErrors) {}
