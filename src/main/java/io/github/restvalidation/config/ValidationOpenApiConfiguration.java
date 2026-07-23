package io.github.restvalidation.config;

import io.github.restvalidation.model.ErrorResponse;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

/**
 * Automatic OpenAPI documentation for error responses.
 */
@Configuration
@ConditionalOnClass(OperationCustomizer.class)
public class ValidationOpenApiConfiguration {

    @Bean
    public OperationCustomizer addErrorResponses() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiResponses responses = operation.getResponses();

            if (!responses.containsKey("400")) {
                responses.addApiResponse("400", createResponse("Bad Request"));
            }
            if (!responses.containsKey("422")) {
                responses.addApiResponse("422", createResponse("Unprocessable Entity (Validation Error)"));
            }
            if (!responses.containsKey("500")) {
                responses.addApiResponse("500", createResponse("Internal Server Error"));
            }

            return operation;
        };
    }

    private ApiResponse createResponse(String description) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        Schema<ErrorResponse> schema = new Schema<>();
        schema.set$ref("#/components/schemas/ErrorResponse");
        mediaType.setSchema(schema);
        content.addMediaType("application/json", mediaType);

        return new ApiResponse()
                .description(description)
                .content(content);
    }
}
