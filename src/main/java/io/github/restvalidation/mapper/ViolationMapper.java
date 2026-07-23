package io.github.restvalidation.mapper;

import io.github.restvalidation.model.ErrorDetail;
import jakarta.validation.ConstraintViolation;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Set;

/**
 * Extension point for mapping validation violations to the library's error model.
 */
public interface ViolationMapper {

    /**
     * Maps Spring MVC FieldErrors to ErrorDetail.
     */
    List<ErrorDetail> fromFieldErrors(List<FieldError> fieldErrors);

    /**
     * Maps Jakarta ConstraintViolations to ErrorDetail.
     */
    List<ErrorDetail> fromConstraintViolations(Set<ConstraintViolation<?>> violations);
}
