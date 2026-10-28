package com.library.repository;
import com.library.model.Loan;
import com.library.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface LoanRepository extends JpaRepository<Loan,Long> {
  List<Loan> findByStatusOrderByIssueDateDesc(LoanStatus status);
  List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate date);
  boolean existsByBookIdAndStatus(Long bookId,LoanStatus status);
  boolean existsByMemberIdAndStatus(Long memberId,LoanStatus status);
  boolean existsByBookIdAndMemberIdAndStatus(Long bookId,Long memberId,LoanStatus status);
  long countByStatus(LoanStatus status);
}
