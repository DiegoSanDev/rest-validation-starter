package io.github.restvalidation.mapper;

import io.github.restvalidation.model.ErrorDetail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("DefaultViolationMapper")
class DefaultViolationMapperTest {

    private DefaultViolationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DefaultViolationMapper();
    }

    @Test
    @DisplayName("should map FieldErrors to ErrorDetail list")
    void shouldMapFieldErrorsToErrorDetailList() {
        var fieldError = new FieldError("userDto", "email", "must not be null");

        List<ErrorDetail> result = mapper.fromFieldErrors(List.of(fieldError));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().field()).isEqualTo("email");
        assertThat(result.getFirst().message()).isEqualTo("must not be null");
    }

    @Test
    @DisplayName("should map ConstraintViolations extracting leaf field name")
    void shouldMapConstraintViolationsExtractingLeafField() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        
        Path.Node node1 = mock(Path.Node.class);
        when(node1.getKind()).thenReturn(ElementKind.METHOD);
        
        Path.Node node2 = mock(Path.Node.class);
        when(node2.getKind()).thenReturn(ElementKind.PARAMETER);
        when(node2.getName()).thenReturn("email");

        Iterator<Path.Node> iterator = List.of(node1, node2).iterator();
        when(path.iterator()).thenReturn(iterator);
        when(path.spliterator()).thenReturn(List.of(node1, node2).spliterator());
        
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("invalid format");

        List<ErrorDetail> result = mapper.fromConstraintViolations(Set.of(violation));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().field()).isEqualTo("email");
        assertThat(result.getFirst().message()).isEqualTo("invalid format");
    }
}
