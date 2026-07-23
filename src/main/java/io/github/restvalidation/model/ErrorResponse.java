package io.github.restvalidation.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Aggregated error response returned to the API client.
 */
@Schema(description = "Standard API error response")
public record ErrorResponse(
    @Schema(description = "Timestamp of the error", example = "2024-05-20T14:30:00")
    LocalDateTime timestamp,

    @Schema(description = "General message describing the error", example = "Invalid request. Please correct the errors and try again.")
    String message,
    
    @Schema(description = "List of detailed errors per field (may be empty for generic errors)")
    List<ErrorDetail> errors,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonAnyGetter
    @Schema(description = "Additional dynamic attributes")
    Map<String, Object> metadata
) {

    public static ErrorResponse of(String message, List<ErrorDetail> errors, Map<String, Object> metadata) {
        return new ErrorResponse(LocalDateTime.now(), message, List.copyOf(errors), metadata != null ? metadata : Map.of());
    }

    public static ErrorResponse of(String message, List<ErrorDetail> errors) {
        return of(message, errors, Map.of());
    }

    public static ErrorResponse of(String message) {
        return of(message, List.of(), Map.of());
    }
}
