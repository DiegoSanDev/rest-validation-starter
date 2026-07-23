package io.github.restvalidation.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a single field-level validation error.
 */
@Schema(description = "Details of a validation error in a specific field")
public record ErrorDetail(
    @Schema(description = "Name of the field that failed validation", example = "email")
    String field,
    
    @Schema(description = "Descriptive error message", example = "must be a well-formed email address")
    String message
) {
    public static ErrorDetail of(String field, String message) {
        return new ErrorDetail(field, message);
    }
}
