package com.example.Netflix.Resolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/resolutions")
public class ResolutionController {
    @Autowired
    private ResolutionService resolutionService;

    @PostMapping
    public ResponseEntity<?> createResolution(@RequestBody Resolution resolution) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            resolutionService.saveResolution(resolution);
            return ResponseEntity.ok("Resolution created successfully: " + resolution);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResolution(@PathVariable int id) {
        try {
            Optional<Resolution> optionalResolution = resolutionService.findResolutionById(id);

            if (optionalResolution.isPresent()) {
                Resolution resolution = optionalResolution.get();

                return ResponseEntity.ok(resolution);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Resolution not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Resolution> findAllResolutions() {
        return resolutionService.findAllResolutions();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResolution(@PathVariable int id) {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            try {
                resolutionService.deleteResolution(id);

                return ResponseEntity.ok("Resolution deleted successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }
}
