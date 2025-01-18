package com.example.Netflix.Exceptions;

public class InvalidContentType extends RuntimeException {
    public InvalidContentType(String message) {
        super(message);
    }
}
