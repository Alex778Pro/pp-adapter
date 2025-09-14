package com.example.pp.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 404:
                return new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Client with given ID not found"
                );
            case 400:
                return new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid request parameters"
                );
            case 500:
                return new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal server error in user service"
                );
            default:
                return new ResponseStatusException(
                        HttpStatus.valueOf(response.status()),
                        "Error while processing request"
                );
        }
    }
}