package com.example.Netflix.JSON;

import java.util.List;

public class ResponseMessages {
    private List<String> response;

    public ResponseMessages() {
    }

    public ResponseMessages(List<String> response) {
        this.response = response;
    }

    public List<String> getResponse() {
        return response;
    }

    public void setResponse(List<String> response) {
        this.response = response;
    }
}
