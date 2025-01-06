package com.example.Netflix.Exceptions;

public class ResourceNotFoundException extends Exception
{
    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
