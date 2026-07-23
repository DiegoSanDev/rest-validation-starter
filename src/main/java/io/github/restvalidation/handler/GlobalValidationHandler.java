package io.github.restvalidation.handler;

import io.github.restvalidation.config.ValidationProperties;
import io.github.restvalidation.mapper.ViolationMapper;
import io.github.restvalidation.model.ErrorDetail;
import io.github.restvalidation.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Central exception handler for REST APIs.
 */
@RestControllerAdvice
public class GlobalValidationHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalValidationHandler.class);

    private final ViolationMapper violationMapper;
    private final ValidationProperties properties;
    private final MessageSource messageSource;
    private final Optional<ErrorResponseCustomizer> customizer;

    public GlobalValidationHandler(ViolationMapper violationMapper, 
                                 ValidationProperties properties,
                                 MessageSource messageSource,
                                 Optional<ErrorResponseCustomizer> customizer) {
        this.violationMapper = violationMapper;
        this.properties = properties;
        this.messageSource = messageSource;
        this.customizer = customizer;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Validation failure in @RequestBody: {}", ex.getBindingResult().getObjectName());
        
        List<ErrorDetail> errors = violationMapper.fromFieldErrors(ex.getBindingResult().getFieldErrors());
        String message = getMessage("rest-validation.validation-failed", properties.getMessages().getValidationFailed());
        
        return ResponseEntity
                .unprocessableEntity()
                .body(buildResponse(message, errors, ex));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation detected: {} violations", ex.getConstraintViolations().size());
        
        List<ErrorDetail> errors = violationMapper.fromConstraintViolations(ex.getConstraintViolations());
        String message = getMessage("rest-validation.validation-failed", properties.getMessages().getValidationFailed());
        
        return ResponseEntity
                .unprocessableEntity()
                .body(buildResponse(message, errors, ex));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.info("Invalid argument type: parameter='{}', value='{}'", ex.getName(), ex.getValue());
        
        String template = getMessage("rest-validation.type-mismatch", properties.getMessages().getTypeMismatch());
        String message = template.formatted(ex.getName(), ex.getValue());
        
        return ResponseEntity
                .badRequest()
                .body(buildResponse(message, List.of(), ex));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        String message = getMessage("rest-validation.not-found", properties.getMessages().getNotFound());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildResponse(message, List.of(), ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error captured by GlobalValidationHandler", ex);
        
        String message = getMessage("rest-validation.internal-error", properties.getMessages().getInternalError());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildResponse(message, List.of(), ex));
    }

    private ErrorResponse buildResponse(String message, List<ErrorDetail> errors, Exception ex) {
        // 1. Static/Global metadata from Bean customizer
        Map<String, Object> metadata = new HashMap<>(customizer
                .map(c -> c.customize(ex))
                .orElse(Map.of()));
        
        // 2. Dynamic/Request metadata from ValidationContext
        metadata.putAll(ValidationContext.getMetadata());
        
        return ErrorResponse.of(message, errors, metadata);
    }

    private String getMessage(String code, String defaultMessage) {
        return messageSource.getMessage(code, null, defaultMessage, LocaleContextHolder.getLocale());
    }
}
