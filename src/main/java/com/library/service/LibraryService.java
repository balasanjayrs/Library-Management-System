package com.library.service;

import com.library.exception.LibraryException;
import com.library.model.*;
import com.library.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LibraryService {
    private static final BigDecimal DAILY_FINE = new BigDecimal("5");
    private final BookRepository books; private final MemberRepository members; private final LoanRepository loans;
    public LibraryService(BookRepository books,MemberRepository members,LoanRepository loans){this.books=books;this.members=members;this.loans=loans;}

    public List<Book> findBooks(String query){return query==null||query.isBlank()?books.findAll():searchBooks(query);}
    public List<Book> searchBooks(String query){
        if(query.matches("\\d+")) return books.findById(Long.valueOf(query)).stream().toList();
        return books.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query,query);
    }
    public Book saveBook(Book book){return books.save(book);} 
    public Book getBook(Long id){return books.findById(id).orElseThrow(() -> new LibraryException("Book not found."));}
    public void deleteBook(Long id){if(loans.existsByBookIdAndStatus(id,LoanStatus.ACTIVE)) throw new LibraryException("An issued book cannot be deleted."); books.delete(getBook(id));}

    public List<Member> findMembers(String query){if(query==null||query.isBlank())return members.findAll(); if(query.matches("\\d+"))return members.findById(Long.valueOf(query)).stream().toList(); return members.findByNameContainingIgnoreCase(query);}
    public Member saveMember(Member member){if(member.getId()!=null){member.setRegistrationDate(getMember(member.getId()).getRegistrationDate());}else if(member.getRegistrationDate()==null){member.setRegistrationDate(LocalDate.now());}return members.save(member);}
    public Member getMember(Long id){return members.findById(id).orElseThrow(() -> new LibraryException("Member not found."));}
    public void deleteMember(Long id){if(loans.existsByMemberIdAndStatus(id,LoanStatus.ACTIVE)) throw new LibraryException("A member with an active loan cannot be deleted."); members.delete(getMember(id));}

    @Transactional
    public Loan issueBook(Long bookId,Long memberId,LocalDate issueDate){
        if(issueDate==null)throw new LibraryException("Issue date is required.");
        if(issueDate.isAfter(LocalDate.now()))throw new LibraryException("Issue date cannot be in the future.");
        Book book=getBook(bookId); Member member=getMember(memberId);
        if(book.getAvailabilityStatus()!=AvailabilityStatus.AVAILABLE)throw new LibraryException("This book is not available.");
        if(loans.existsByBookIdAndMemberIdAndStatus(bookId,memberId,LoanStatus.ACTIVE))throw new LibraryException("This member already has an active loan for this book.");
        Loan loan=new Loan(book,member,issueDate); book.setAvailabilityStatus(AvailabilityStatus.ISSUED);
        books.save(book); return loans.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId,LocalDate returnDate){
        if(returnDate==null)throw new LibraryException("Return date is required.");
        Loan loan=loans.findById(loanId).orElseThrow(() -> new LibraryException("Loan not found."));
        if(loan.getStatus()!=LoanStatus.ACTIVE)throw new LibraryException("This loan has already been returned.");
        if(returnDate.isBefore(loan.getIssueDate()))throw new LibraryException("Return date cannot be before the issue date.");
        loan.setReturnDate(returnDate); loan.setFineAmount(calculateFine(loan.getDueDate(),returnDate)); loan.setStatus(LoanStatus.RETURNED);
        loan.getBook().setAvailabilityStatus(AvailabilityStatus.AVAILABLE); loans.save(loan); return loan;
    }

    public BigDecimal calculateFine(LocalDate dueDate,LocalDate returnDate){long days=Math.max(0,ChronoUnit.DAYS.between(dueDate,returnDate));return DAILY_FINE.multiply(BigDecimal.valueOf(days));}
    public long overdueDays(Loan loan){return Math.max(0,ChronoUnit.DAYS.between(loan.getDueDate(),LocalDate.now()));}
    public List<Loan> activeLoans(){return loans.findByStatusOrderByIssueDateDesc(LoanStatus.ACTIVE);} 
    public List<Loan> allLoans(){return loans.findAll();}
    public List<Loan> overdueLoans(){return loans.findByStatusAndDueDateBefore(LoanStatus.ACTIVE,LocalDate.now());}
    public DashboardStats dashboard(){return new DashboardStats(books.count(),books.countByAvailabilityStatus(AvailabilityStatus.AVAILABLE),loans.countByStatus(LoanStatus.ACTIVE),members.count(),overdueLoans().size());}
    public record DashboardStats(long totalBooks,long availableBooks,long issuedBooks,long totalMembers,long overdueBooks){}
}
