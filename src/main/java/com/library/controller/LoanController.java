// src/main/java/com/library/controller/LoanController.java
package com.library.controller;

import com.library.model.Loan;
import com.library.service.LibraryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LibraryService libraryService;

    public record LoanRequest(
        @NotNull(message = "El bookId es obligatorio")  Long bookId,
        @NotBlank(message = "El nombre es obligatorio") String borrowerName,
        @Email(message = "Email inválido")               String borrowerEmail
    ) {}

    @PostMapping
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody LoanRequest request) {
        Loan loan = libraryService.borrowBook(
            request.bookId(),
            request.borrowerName(),
            request.borrowerEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(libraryService.returnBook(id));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Loan>> getOverdueLoans() {
        return ResponseEntity.ok(libraryService.getOverdueLoans());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Loan>> getActiveLoans() {
        return ResponseEntity.ok(libraryService.getActiveLoans());
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(libraryService.getAllLoans());
    }
}
