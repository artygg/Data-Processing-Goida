package com.example.Netflix.Resolutions;

import com.example.Netflix.Content.Content;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Resolution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "resolution_id")
    private Integer id;
    private String name;
    @ManyToMany
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
