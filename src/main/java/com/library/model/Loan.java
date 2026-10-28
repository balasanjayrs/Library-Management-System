package com.library.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="loans", indexes=@Index(name="idx_loan_status", columnList="status"))
public class Loan {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="book_id", nullable=false)
    private Book book;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="member_id", nullable=false)
    private Member member;
    @Column(nullable=false) private LocalDate issueDate;
    @Column(nullable=false) private LocalDate dueDate;
    private LocalDate returnDate;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal fineAmount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LoanStatus status = LoanStatus.ACTIVE;

    public Loan() {}
    public Loan(Book book, Member member, LocalDate issueDate) { this.book=book; this.member=member; this.issueDate=issueDate; this.dueDate=issueDate.plusDays(14); }
    public Long getId(){return id;} public void setId(Long id){this.id=id;} public Book getBook(){return book;} public void setBook(Book book){this.book=book;} public Member getMember(){return member;} public void setMember(Member member){this.member=member;} public LocalDate getIssueDate(){return issueDate;} public void setIssueDate(LocalDate issueDate){this.issueDate=issueDate;} public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate dueDate){this.dueDate=dueDate;} public LocalDate getReturnDate(){return returnDate;} public void setReturnDate(LocalDate returnDate){this.returnDate=returnDate;} public BigDecimal getFineAmount(){return fineAmount;} public void setFineAmount(BigDecimal fineAmount){this.fineAmount=fineAmount;} public LoanStatus getStatus(){return status;} public void setStatus(LoanStatus status){this.status=status;}
}
