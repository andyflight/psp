package com.example.psp.exception.handler;

import jakarta.validation.ValidationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Customizes the error attributes returned in case of exceptions.
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request); // Get the underlying exception

        Map<String, Object> errorAttributes = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order
        HttpStatus status = determineHttpStatus(error);

        errorAttributes.put("type", request.path()); // URI
        errorAttributes.put("status", status.value()); // HTTP status
        errorAttributes.put("title", error.getClass().getSimpleName()); // Exception class name
        errorAttributes.put("description", error.getMessage()); // Exception message

        return errorAttributes;
    }

    /**
     * Determines the appropriate HTTP status based on the type of exception.
     * @param error throwable
     * @return HttpStatus
     */
    private HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof ValidationException ||
                error instanceof IllegalArgumentException ||
                error instanceof IllegalStateException ||
                error instanceof NullPointerException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
