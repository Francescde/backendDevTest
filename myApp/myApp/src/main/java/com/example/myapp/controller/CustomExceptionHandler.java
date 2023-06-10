package com.example.myapp.controller;

import com.example.myapp.model.ErrorResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {
    private final ObjectMapper objectMapper;

    public CustomExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleException(Exception ex) {
        if (ex instanceof HttpStatusCodeException) {
            HttpStatusCodeException clientErrorException = (HttpStatusCodeException) ex;
            String responseBody = clientErrorException.getResponseBodyAsString();
            Map<String,Object> errorBody = parseErrorMessage(responseBody);
            return new ResponseEntity<>(errorBody, clientErrorException.getStatusCode());
        }

        Map<String,Object> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError("Not Found");
        errorResponse.setMessage("The requested resource was not found");

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private Map<String,Object> parseErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }
        try {
            // Attempt to parse the response body as JSON
            Map<String,Object> errorMessage = objectMapper.readValue(responseBody, Map.class);
            return errorMessage;
        } catch (IOException e) {
            // If parsing as JSON fails, assume the response body is empty
            return null;
        }
    }
}

