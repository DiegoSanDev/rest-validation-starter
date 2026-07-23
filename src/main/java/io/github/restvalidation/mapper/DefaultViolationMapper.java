package io.github.restvalidation.mapper;

import io.github.restvalidation.model.ErrorDetail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * Default implementation of ViolationMapper.
 */
public class DefaultViolationMapper implements ViolationMapper {

    @Override
    public List<ErrorDetail> fromFieldErrors(List<FieldError> fieldErrors) {
        if (fieldErrors == null) return List.of();
        
        return fieldErrors.stream()
                .map(fe -> ErrorDetail.of(fe.getField(), fe.getDefaultMessage()))
                .toList();
    }

    @Override
    public List<ErrorDetail> fromConstraintViolations(Set<ConstraintViolation<?>> violations) {
        if (violations == null) return List.of();

        return violations.stream()
                .map(cv -> ErrorDetail.of(
                        extractLeafPropertyName(cv),
                        cv.getMessage()
                ))
                .toList();
    }

    private String extractLeafPropertyName(ConstraintViolation<?> violation) {
        Iterable<Path.Node> path = violation.getPropertyPath();
        
        return StreamSupport.stream(path.spliterator(), false)
                .filter(node -> ElementKind.PROPERTY.equals(node.getKind()) || ElementKind.PARAMETER.equals(node.getKind()))
                .map(Path.Node::getName)
                .reduce((first, second) -> second)
                .orElseGet(() -> violation.getPropertyPath().toString());
    }
}
