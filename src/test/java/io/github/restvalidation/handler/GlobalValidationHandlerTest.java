package io.github.restvalidation.handler;

import io.github.restvalidation.config.ValidationProperties;
import io.github.restvalidation.mapper.ViolationMapper;
import io.github.restvalidation.model.ErrorDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalValidationHandler")
class GlobalValidationHandlerTest {

    private GlobalValidationHandler handler;

    @Mock
    private ViolationMapper violationMapper;

    @Mock
    private MessageSource messageSource;

    private ValidationProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ValidationProperties();
        handler = new GlobalValidationHandler(violationMapper, properties, messageSource, Optional.empty());
    }

    @Test
    @DisplayName("should return 422 with field errors using mapper mock")
    void shouldReturn422ForMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        
        List<ErrorDetail> mockErrors = List.of(ErrorDetail.of("name", "error"));
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(violationMapper.fromFieldErrors(any())).thenReturn(mockErrors);
        
        when(messageSource.getMessage(eq("rest-validation.validation-failed"), any(), any(), any(Locale.class)))
                .thenReturn("Custom Message");

        var response = handler.handleMethodArgumentNotValid(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().errors()).isEqualTo(mockErrors);
        assertThat(response.getBody().message()).isEqualTo("Custom Message");
    }

    @Test
    @DisplayName("should return 400 with formatted message for TypeMismatch")
    void shouldReturn400ForTypeMismatch() {
        var ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        when(ex.getValue()).thenReturn("abc");
        
        when(messageSource.getMessage(eq("rest-validation.type-mismatch"), any(), any(), any(Locale.class)))
                .thenReturn("Error in field %s with value %s");

        var response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Error in field id with value abc");
    }

    @Test
    @DisplayName("should return 500 for generic Exception")
    void shouldReturn500ForGenericException() {
        when(messageSource.getMessage(eq("rest-validation.internal-error"), any(), any(), any(Locale.class)))
                .thenReturn("Internal error");

        var response = handler.handleGenericException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("Internal error");
    }
}
