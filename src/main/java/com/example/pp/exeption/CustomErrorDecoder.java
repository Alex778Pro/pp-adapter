package com.example.pp.exeption;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with given ID not found"
            );
            case 400 -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request parameters"
            );
            case 500 -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error in user service"
            );
            default -> new ResponseStatusException(
                    HttpStatus.valueOf(response.status()),
                    "Error while processing request"
            );
        };
    }
}
