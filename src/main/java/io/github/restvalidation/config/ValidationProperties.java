package io.github.restvalidation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the REST Validation Starter.
 */
@ConfigurationProperties(prefix = "rest-validation")
public class ValidationProperties {

    private final Messages messages = new Messages();

    public Messages getMessages() {
        return messages;
    }

    public static class Messages {
        /**
         * Message for validation failures (422).
         */
        private String validationFailed = "Invalid request. Please correct the errors and try again.";

        /**
         * Message for type mismatch errors (400).
         */
        private String typeMismatch = "Invalid data type for parameter '%s'. Received value: '%s'.";

        /**
         * Message for resource not found (404).
         */
        private String notFound = "Resource not found.";

        /**
         * Message for unexpected internal errors (500).
         */
        private String internalError = "An unexpected error occurred. Please try again later.";

        public String getValidationFailed() {
            return validationFailed;
        }

        public void setValidationFailed(String validationFailed) {
            this.validationFailed = validationFailed;
        }

        public String getTypeMismatch() {
            return typeMismatch;
        }

        public void setTypeMismatch(String typeMismatch) {
            this.typeMismatch = typeMismatch;
        }

        public String getNotFound() {
            return notFound;
        }

        public void setNotFound(String notFound) {
            this.notFound = notFound;
        }

        public String getInternalError() {
            return internalError;
        }

        public void setInternalError(String internalError) {
            this.internalError = internalError;
        }
    }
}
