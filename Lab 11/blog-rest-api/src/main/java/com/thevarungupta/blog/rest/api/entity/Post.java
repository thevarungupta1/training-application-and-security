package com.thevarungupta.blog.rest.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "posts",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"title"}
                )
        }
)
public class Post {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @NotEmpty(message = "Post title should not be empty")
    @Size(min = 3, message = "Post title should have at least 3 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotEmpty(message = "Post description should not be empty")
    @Size(min = 10, message = "Post description should have at least 10 characters")
    @Column(name = "description", nullable = false)
    private String description;

    @NotEmpty(message = "Post content should not be empty")
    @Column(name = "content", nullable = false)
    private String content;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Comment> comments = new HashSet<>();
}
