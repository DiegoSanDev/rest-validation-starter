package io.github.restvalidation.handler;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility to store dynamic metadata for the current request's error response.
 * Data stored here will be automatically included in the 'metadata' field of ErrorResponse.
 */
public final class ValidationContext {

    private static final String METADATA_KEY = "io.github.restvalidation.METADATA";

    private ValidationContext() {}

    /**
     * Adds a key-value pair to the current request's error metadata.
     */
    public static void put(String key, Object value) {
        getMetadata().put(key, value);
    }

    /**
     * Adds multiple entries to the current request's error metadata.
     */
    public static void putAll(Map<String, Object> data) {
        getMetadata().putAll(data);
    }

    /**
     * Clears all metadata for the current request.
     */
    public static void clear() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            attrs.removeAttribute(METADATA_KEY, RequestAttributes.SCOPE_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> getMetadata() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return new HashMap<>();
        }

        Map<String, Object> metadata = (Map<String, Object>) attrs.getAttribute(METADATA_KEY, RequestAttributes.SCOPE_REQUEST);
        if (metadata == null) {
            metadata = new HashMap<>();
            attrs.setAttribute(METADATA_KEY, metadata, RequestAttributes.SCOPE_REQUEST);
        }
        return metadata;
    }
}
