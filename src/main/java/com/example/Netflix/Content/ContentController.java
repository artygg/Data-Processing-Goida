package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
public class ContentController
{
    private final ContentService contentService;

    public ContentController(ContentService contentService)
    {
        this.contentService = contentService;
    }

    @GetMapping
    public List<Content> getAllContents()
    {
        return contentService.getAllContents();
    }

    @GetMapping("/{id}")
    public Content getContentById(@PathVariable Long id) throws ResourceNotFoundException
    {
        return contentService.getContentById(id);
    }

    @GetMapping("/genre/{genreName}")
    public List<Content> getContentsByGenre(@PathVariable String genreName)
    {
        return contentService.getContentsByGenre(genreName);
    }

    @PostMapping
    public ResponseEntity<?> createContent(@RequestBody Content content)
    {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            return ResponseEntity.ok(contentService.createContent(content));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContent(@PathVariable Long id, @RequestBody Content updatedContent) throws ResourceNotFoundException
    {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            return ResponseEntity.ok(contentService.updateContent(id, updatedContent));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContent(@PathVariable Long id)
    {
        try {
            ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            contentService.deleteContent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request: " + e.getMessage());
        }
    }
}
