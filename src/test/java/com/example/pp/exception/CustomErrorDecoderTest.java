package com.example.pp.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomErrorDecoderTest {
    private CustomErrorDecoder customErrorDecoder;

    @BeforeEach
    void setUp() {
        customErrorDecoder = new CustomErrorDecoder();
    }

    @Test
    void testImplementErrorDecoder() {
        assertTrue(customErrorDecoder instanceof ErrorDecoder);
    }

    @Test
    void testDecode_Status404() {
        String methodKey = "UsersApiClient#getClientById(String)";
        Response response = mock(Response.class);
        when(response.status()).thenReturn(404);

        Exception exception = customErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof ResponseStatusException);

        ResponseStatusException responseStatusException = (ResponseStatusException) exception;
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
        assertEquals("Client with given ID not found", responseStatusException.getReason());
    }

    @Test
    void testDecode_Status400() {
        String methodKey = "UsersApiClient#getClientById(String)";
        Response response = mock(Response.class);
        when(response.status()).thenReturn(400);

        Exception exception = customErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof ResponseStatusException);

        ResponseStatusException responseStatusException = (ResponseStatusException) exception;
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
        assertEquals("Invalid request parameters", responseStatusException.getReason());
    }

    @Test
    void testDecode_Status500() {
        String methodKey = "UsersApiClient#getClientById(String)";
        Response response = mock(Response.class);
        when(response.status()).thenReturn(500);

        Exception exception = customErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof ResponseStatusException);

        ResponseStatusException responseStatusException = (ResponseStatusException) exception;
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatusException.getStatusCode());
        assertEquals("Internal server error in user service", responseStatusException.getReason());
    }

    @Test
    void testDecode_Status401() {
        String methodKey = "UsersApiClient#getClientById(String)";
        Response response = mock(Response.class);
        when(response.status()).thenReturn(401);

        Exception exception = customErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof ResponseStatusException);

        ResponseStatusException responseStatusException = (ResponseStatusException) exception;
        assertEquals(HttpStatus.UNAUTHORIZED, responseStatusException.getStatusCode());
        assertEquals("Error while processing request", responseStatusException.getReason());
    }

    @Test
    void testDecode_Status403() {
        String methodKey = "UsersApiClient#getClientById(String)";
        Response response = mock(Response.class);
        when(response.status()).thenReturn(403);

        Exception exception = customErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof ResponseStatusException);

        ResponseStatusException responseStatusException = (ResponseStatusException) exception;
        assertEquals(HttpStatus.FORBIDDEN, responseStatusException.getStatusCode());
        assertEquals("Error while processing request", responseStatusException.getReason());
    }
}