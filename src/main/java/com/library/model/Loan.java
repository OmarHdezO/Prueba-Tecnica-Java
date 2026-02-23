package com.library.model;

import java.time.LocalDate;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Entity
@Table(name = "loans")

public class Loan{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotBlank
    @Column(nullable = false)
    private String borrowerName;

    @NotBlank
    @Column(nullable = false)
    private String borrowerEmail;

    @NotBlank
    @Column(nullable = false)
    private LocalDate loanDate;

    @NotBlank
    @Column(nullable = false)
    private LocalDate dueDate;

    @NotBlank
    @Column(nullable = false)
    private LocalDate returnDate;

    @PrePersist
    protected void onCreate() {
        if (loanDate == null) loanDate = LocalDate.now();
        if (dueDate == null) dueDate = loanDate.plusDays(14); // Default due date is 14 days after loan date
    }

    public boolean isOverdue(){
        return returnDate == null && dueDate.isBefore(LocalDate.now());
    }


    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getBorrowerEmail() {
        return borrowerEmail;
    }

    public void setBorrowerEmail(String borrowerEmail) {
        this.borrowerEmail = borrowerEmail;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}


