package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.library.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
 // 1. Buscar préstamos por email del solicitante
 List<Loan> findByBorrowerEmail(String email);

 // 2. Préstamos activos (sin devolver)
 List<Loan> findByReturnDateIsNull();

 // 3. Préstamos vencidos (JPQL)
 @Query("SELECT l FROM Loan l WHERE " +
 "l.dueDate < CURRENT_DATE AND " +
 "l.returnDate IS NULL")
 List<Loan> findOverdueLoans();

 // 4. Verificar si un libro está prestado
 boolean existsByBookIdAndReturnDateIsNull(Long bookId);
}
