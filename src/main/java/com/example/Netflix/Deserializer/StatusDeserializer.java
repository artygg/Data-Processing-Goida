package com.example.Netflix.Deserializer;

import com.example.Netflix.Exceptions.InvalidContentType;
import com.example.Netflix.enums.ContentType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StatusDeserializer extends JsonDeserializer<ContentType> {
    @Override
    public ContentType deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            return ContentType.valueOf(parser.getText().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidContentType("Invalid content type value: " + parser.getText());
        }
    }
}
