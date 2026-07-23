package io.github.restvalidation.config;

import io.github.restvalidation.handler.ErrorResponseCustomizer;
import io.github.restvalidation.handler.GlobalValidationHandler;
import io.github.restvalidation.mapper.DefaultViolationMapper;
import io.github.restvalidation.mapper.ViolationMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * Spring Boot auto-configuration for the REST Validation Starter.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(ValidationProperties.class)
public class ValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ViolationMapper.class)
    public ViolationMapper violationMapper() {
        return new DefaultViolationMapper();
    }

    @Bean
    @ConditionalOnMissingBean(GlobalValidationHandler.class)
    public GlobalValidationHandler globalValidationHandler(ViolationMapper violationMapper, 
                                                         ValidationProperties properties,
                                                         MessageSource messageSource,
                                                         Optional<ErrorResponseCustomizer> customizer) {
        return new GlobalValidationHandler(violationMapper, properties, messageSource, customizer);
    }
}
