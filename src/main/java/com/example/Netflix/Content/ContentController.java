package com.example.Netflix.Content;

import com.example.Netflix.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Content createContent(@RequestBody Content content)
    {
        return contentService.createContent(content);
    }

    @PutMapping("/{id}")
    public Content updateContent(@PathVariable Long id, @RequestBody Content updatedContent) throws ResourceNotFoundException
    {
        return contentService.updateContent(id, updatedContent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id)
    {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}
