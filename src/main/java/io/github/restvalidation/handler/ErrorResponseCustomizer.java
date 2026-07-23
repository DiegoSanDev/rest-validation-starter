package io.github.restvalidation.handler;

import java.util.Map;

/**
 * Strategy interface to customize the error response with additional attributes.
 * Users can implement this to add fields like 'traceId', 'path', or 'internalCode'.
 */
@FunctionalInterface
public interface ErrorResponseCustomizer {

    /**
     * Returns a map of additional attributes to be included in the error response.
     * @param ex the exception being handled
     * @return a map of extra fields, or an empty map if none.
     */
    Map<String, Object> customize(Exception ex);
}
