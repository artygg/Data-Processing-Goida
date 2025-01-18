package com.example.Netflix.Resolutions;

import com.example.Netflix.Content.Content;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
public class Resolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resolution_id")
    private Integer id;
    @NotNull(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Title must contain only letters and numbers")
    private String name;
    @NotNull(message = "Content is required")
    @ManyToMany()
    @JoinTable(
            name = "quality_ranges",
            joinColumns = @JoinColumn(name = "resolution_id"),
            inverseJoinColumns = @JoinColumn(name = "content_id")
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Content> contents;

    public Resolution() {

    }

    public Resolution(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Content> getContents() {
        return this.contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}
