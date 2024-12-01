package com.example.Netflix.Exceptions;

public class ProfileLimitReached extends Exception{
    public ProfileLimitReached() {
        super("You've reached the limit of your profiles");
    }
}
