package com.example.Netflix.Generalization;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

public abstract class BaseController<T, ID> {

    protected abstract BaseService<T, ID> getService();

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<T> getById(@PathVariable ID id) {
        Optional<T> optionalEntity = getService().findById(id);

        if (optionalEntity.isPresent()) {
            T entity = optionalEntity.get();

            return ResponseEntity.ok(entity);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<T>> getAll() {
        List<T> entities = getService().findAll();

        return ResponseEntity.ok(entities);
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> create(@Valid @RequestBody T entity) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: User not authenticated");
            }

            T createdEntity = getService().create(entity);

            return ResponseEntity.status(201).body(createdEntity);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> update(@PathVariable ID id,
                                    @Valid @RequestBody T entity) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: User not authenticated");
            }

            T updatedEntity = getService().update(id, entity);

            return ResponseEntity.ok(updatedEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating content: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> delete(@PathVariable ID id) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUsername();
            getService().delete(id);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }
}
