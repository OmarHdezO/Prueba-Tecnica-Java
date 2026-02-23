// src/main/java/com/library/model/Book.java
package com.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "El ISBN es obligatorio")
    @Size(min = 13, max = 13, message = "El ISBN debe tener exactamente 13 caracteres")
    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @NotNull(message = "El año de publicación es obligatorio")
    @Min(value = 1000, message = "El año debe ser mayor a 1000")
    @Max(value = 2026, message = "El año no puede ser mayor al actual")
    @Column(name = "publication_year")
    private Integer publicationYear;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // JPA lifecycle hook: garantiza defaults antes de persistir
    @PrePersist
    protected void onCreate() {
        if (status == null) status = BookStatus.AVAILABLE;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
