package com.example.Netflix.Content.Genre;

import com.example.Netflix.Content.Content;
import com.example.Netflix.Content.GenreBridge.GenreBridge;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//name    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<GenreBridge> genreBridges;
    @ManyToOne
    @JsonBackReference
    private Content content;


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

//    public Set<GenreBridge> getGenreBridges() { return genreBridges; }
//    public void setGenreBridges(Set<GenreBridge> genreBridges) { this.genreBridges = genreBridges; }genreBridges

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
