package com.sep.rookieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @Column(name = "genre_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String genreId;

    @Column(name = "genre_name", length = 50)
    private String genreName;

    @Column(name = "description", length = 250)
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "genres")
    private List<Book> books;
}

