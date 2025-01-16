package com.example.Netflix.GlobalExceptionHandlers;

import com.example.Netflix.Exceptions.InvalidContentType;
import com.example.Netflix.Exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleInvalidEnumValueException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("!!!!!!!!!!");
    }
}
