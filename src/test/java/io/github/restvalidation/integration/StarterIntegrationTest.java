package io.github.restvalidation.integration;

import io.github.restvalidation.config.ValidationAutoConfiguration;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StarterIntegrationTest.TestController.class)
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
@DisplayName("Starter Integration Test")
class StarterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should intercept validation error and return standardized JSON")
    void shouldInterceptValidationError() throws Exception {
        String invalidJson = "{\"name\": \"\"}";

        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
    }

    @Configuration
    @RestController
    static class TestController {
        @PostMapping("/test")
        public void test(@Valid @RequestBody TestDto dto) {}
    }

    record TestDto(@NotBlank(message = "must not be blank") String name) {}
}
