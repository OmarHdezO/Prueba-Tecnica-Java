// src/main/java/com/library/service/LibraryService.java
package com.library.service;

import com.library.model.*;
import com.library.repository.*;

import exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class LibraryService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

//       1. Registrar nuevo libro 
    public Book registerBook(Book book) {
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateIsbnException(book.getIsbn());
        }
        book.setCreatedAt(LocalDateTime.now());
        book.setStatus(BookStatus.AVAILABLE);
        return bookRepository.save(book);
    }

    //   2. Realizar préstamo 
    public Loan borrowBook(Long bookId, String borrowerName, String borrowerEmail) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException(bookId));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BookNotAvailableException(book.getIsbn());
        }

        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        Loan loan = new Loan();
            loan.setBook(book);
            loan.setBorrowerName(borrowerName);
            loan.setBorrowerEmail(borrowerEmail);
            loan.setLoanDate(today);
            loan.setDueDate(today.plusDays(14));

        return loanRepository.save(loan);
    }

    //    3. Devolver libro 
    public Book returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con id: " + loanId));

        if (loan.getReturnDate() != null) {
            throw new RuntimeException("Este préstamo ya fue devuelto previamente");
        }

        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);

        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        return bookRepository.save(book);
    }

    //     4. Buscar libros 
    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll();
        }
        return bookRepository.searchByTitleOrAuthor(keyword.trim());
    }

    //     5. Estadísticas 
    @Transactional(readOnly = true)
    public Map<String, Object> getLibraryStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalBooks",     bookRepository.count());
        stats.put("availableBooks", bookRepository.findByStatus(BookStatus.AVAILABLE).size());
        stats.put("borrowedBooks",  bookRepository.findByStatus(BookStatus.BORROWED).size());
        stats.put("activeLoans",    loanRepository.findByReturnDateIsNull().size());
        stats.put("overdueLoans",   loanRepository.findOverdueLoans().size());
        return stats;
    }

    //  Helpers extra 
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() { return bookRepository.findAll(); }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Book> getAvailableBooks() {
        return bookRepository.findByStatus(BookStatus.AVAILABLE);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BookNotAvailableException(book.getIsbn());
        }
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans()    { return loanRepository.findAll(); }

    @Transactional(readOnly = true)
    public List<Loan> getActiveLoans() { return loanRepository.findByReturnDateIsNull(); }

    @Transactional(readOnly = true)
    public List<Loan> getOverdueLoans() { return loanRepository.findOverdueLoans(); }
}
