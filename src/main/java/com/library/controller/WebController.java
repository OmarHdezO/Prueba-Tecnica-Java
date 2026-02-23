// src/main/java/com/library/controller/WebController.java
package com.library.controller;

import com.library.model.Book;
import com.library.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final LibraryService libraryService;

    // ── Dashboard ─────────────────────────────────────────────────────────
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("stats", libraryService.getLibraryStats());
        model.addAttribute("recentLoans", libraryService.getActiveLoans());
        return "index";
    }

    // ── Libros ────────────────────────────────────────────────────────────
    @GetMapping("/books")
    public String listBooks(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("books", libraryService.searchBooks(q));
        model.addAttribute("keyword", q);
        return "books/list";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid @ModelAttribute Book book, BindingResult result,
                           RedirectAttributes ra, Model model) {
        if (result.hasErrors()) return "books/form";
        try {
            libraryService.registerBook(book);
            ra.addFlashAttribute("successMessage", "¡Libro registrado exitosamente!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "books/form";
        }
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes ra) {
        try {
            libraryService.deleteBook(id);
            ra.addFlashAttribute("successMessage", "Libro eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/books";
    }

    // ── Préstamos ─────────────────────────────────────────────────────────
    @GetMapping("/loans")
    public String listLoans(Model model) {
        model.addAttribute("loans", libraryService.getActiveLoans());
        return "loans/list";
    }

    @GetMapping("/loans/new")
    public String newLoanForm(@RequestParam(required = false) Long bookId, Model model) {
        model.addAttribute("availableBooks", libraryService.getAvailableBooks());
        model.addAttribute("preselectedBookId", bookId);
        return "loans/form";
    }

    @PostMapping("/loans/save")
    public String saveLoan(@RequestParam Long bookId,
                           @RequestParam String borrowerName,
                           @RequestParam String borrowerEmail,
                           RedirectAttributes ra) {
        try {
            libraryService.borrowBook(bookId, borrowerName, borrowerEmail);
            ra.addFlashAttribute("successMessage", "¡Préstamo registrado exitosamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/loans/new";
        }
        return "redirect:/loans";
    }

    @PostMapping("/loans/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes ra) {
        try {
            libraryService.returnBook(id);
            ra.addFlashAttribute("successMessage", "Libro devuelto correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/loans";
    }
}
