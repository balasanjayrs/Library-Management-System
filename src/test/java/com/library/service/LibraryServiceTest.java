package com.library.service;

import com.library.exception.LibraryException;
import com.library.model.*;
import com.library.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    @Mock BookRepository books; @Mock MemberRepository members; @Mock LoanRepository loans;
    LibraryService service;
    Book book; Member member;

    @BeforeEach void setUp(){service=new LibraryService(books,members,loans);book=new Book();book.setId(1L);book.setTitle("Java");book.setAuthor("Author");book.setCategory("Technology");member=new Member();member.setId(2L);member.setName("Student");member.setEmail("student@example.edu");member.setPhone("123456");}

    @Test void addsAndSearchesBooks(){when(books.save(book)).thenReturn(book);when(books.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("Java","Java")).thenReturn(List.of(book));assertSame(book,service.saveBook(book));assertEquals(List.of(book),service.findBooks("Java"));}
    @Test void registersMembers(){member.setId(null);when(members.save(member)).thenReturn(member);assertSame(member,service.saveMember(member));assertEquals(LocalDate.now(),member.getRegistrationDate());}
    @Test void issueUsesFourteenDayDueDate(){when(books.findById(1L)).thenReturn(Optional.of(book));when(members.findById(2L)).thenReturn(Optional.of(member));when(loans.existsByBookIdAndMemberIdAndStatus(1L,2L,LoanStatus.ACTIVE)).thenReturn(false);when(loans.save(any())).thenAnswer(i->i.getArgument(0));Loan loan=service.issueBook(1L,2L,LocalDate.of(2026,1,10));assertEquals(LocalDate.of(2026,1,24),loan.getDueDate());assertEquals(AvailabilityStatus.ISSUED,book.getAvailabilityStatus());}
    @Test void rejectsUnavailableBook(){book.setAvailabilityStatus(AvailabilityStatus.ISSUED);when(books.findById(1L)).thenReturn(Optional.of(book));when(members.findById(2L)).thenReturn(Optional.of(member));assertThrows(LibraryException.class,()->service.issueBook(1L,2L,LocalDate.now()));}
    @Test void returnsAndCalculatesZeroFine(){Loan loan=issueLoan();when(loans.findById(10L)).thenReturn(Optional.of(loan));Loan result=service.returnBook(10L,loan.getDueDate());assertEquals("0",result.getFineAmount().toPlainString());assertEquals(LoanStatus.RETURNED,result.getStatus());assertEquals(AvailabilityStatus.AVAILABLE,book.getAvailabilityStatus());}
    @Test void calculatesOneAndFiveDayFines(){assertEquals("5",service.calculateFine(LocalDate.of(2026,1,10),LocalDate.of(2026,1,11)).toPlainString());assertEquals("25",service.calculateFine(LocalDate.of(2026,1,10),LocalDate.of(2026,1,15)).toPlainString());}
    @Test void preventsRepeatedReturn(){Loan loan=issueLoan();loan.setStatus(LoanStatus.RETURNED);when(loans.findById(10L)).thenReturn(Optional.of(loan));assertThrows(LibraryException.class,()->service.returnBook(10L,LocalDate.now()));}
    @Test void detectsOverdueLoans(){Loan loan=issueLoan();loan.setDueDate(LocalDate.now().minusDays(3));when(loans.findByStatusAndDueDateBefore(eq(LoanStatus.ACTIVE),any())).thenReturn(List.of(loan));assertEquals(1,service.overdueLoans().size());assertEquals(3,service.overdueDays(loan));}
    private Loan issueLoan(){book.setAvailabilityStatus(AvailabilityStatus.ISSUED);Loan loan=new Loan(book,member,LocalDate.now().minusDays(20));loan.setId(10L);return loan;}
}
