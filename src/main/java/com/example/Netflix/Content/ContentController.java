package com.example.Netflix.Content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content")
@CrossOrigin(origins = "*")
public class ContentController
{
    private final ContentService contentService;
    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public List<Content> getAllContents() {
        try
        {
            return contentService.getAllContents();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<Content> createContent(@RequestBody Content content) {
        try {
            System.out.println("Received POST request with content: " + content);
            Content savedContent = contentService.saveContent(content);
            return ResponseEntity.ok(savedContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

//    @PostMapping("/assign-genres")
//    public ResponseEntity<String> assignGenres(@RequestBody GenreAssignmentRequest request) {
//        try {
//            contentService.assignGenresToContent(request.getContentId(), request.getGenreIds());
//            return ResponseEntity.ok("Genres assigned successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
}
