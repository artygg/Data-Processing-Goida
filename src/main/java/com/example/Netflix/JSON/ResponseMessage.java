package com.example.Netflix.JSON;

public class ResponseMessage {
    private String response;

    public ResponseMessage() {

    }

    public ResponseMessage(String response) {
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
