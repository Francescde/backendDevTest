package com.example.myapp.controller;

import com.example.myapp.controller.CustomExceptionHandler;
import com.example.myapp.model.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CustomExceptionHandlerTest {

    private CustomExceptionHandler customExceptionHandler;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        customExceptionHandler = new CustomExceptionHandler( new ObjectMapper());
    }

    @Test
    public void testHandleException_HttpStatusCodeException() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> responseBodyMap = new HashMap<>();
        responseBodyMap.put("error", "Invalid input");

        String jsonString = objectMapper.writeValueAsString(responseBodyMap);
        HttpStatusCodeException exception = mock(HttpClientErrorException.class);
        when(exception.getResponseBodyAsString()).thenReturn(jsonString);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        ResponseEntity<Map<String, Object>> expectedResponse = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBodyMap);

        ResponseEntity<Map<String, Object>> response = customExceptionHandler.handleException(exception);

        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        assertEquals(expectedResponse.getBody().get("error"), response.getBody().get("error"));
    }


    @Test
    public void testHandleException_GeneralException() {
        Exception exception = new Exception("Internal Server Error");
        ResponseEntity<Map<String, Object>> response = customExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().get("message"));
    }

    @Test
    public void testHandleNoHandlerFoundException() {
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/api/users", null);
        ResponseEntity<ErrorResponse> response = customExceptionHandler.handleNoHandlerFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("The requested resource was not found", errorResponse.getMessage());
    }
}



