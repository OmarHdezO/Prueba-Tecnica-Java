package com.library.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.library.model.Book;
import com.library.model.BookStatus;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
 // 1. Buscar por ISBN exacto
 Optional<Book> findByIsbn(String isbn);

 // 2. Buscar por estado
 List<Book> findByStatus(BookStatus status);

 // 3. Buscar libros por autor (case insensitive, contiene)
 List<Book> findByAuthorContainingIgnoreCase(String author);

 // 4. Búsqueda por título O autor (JPQL)
 @Query("SELECT b FROM Book b WHERE " +
 "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
 "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
 List<Book> searchByTitleOrAuthor(@Param("keyword") String keyword);
}