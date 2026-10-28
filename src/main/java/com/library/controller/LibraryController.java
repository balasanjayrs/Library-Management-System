package com.library.controller;

import com.library.exception.LibraryException;
import com.library.model.*;
import com.library.repository.*;
import com.library.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
public class LibraryController {
    private final LibraryService service; private final BookRepository books; private final MemberRepository members;
    public LibraryController(LibraryService service,BookRepository books,MemberRepository members){this.service=service;this.books=books;this.members=members;}

    @GetMapping("/") public String dashboard(Model m){m.addAttribute("stats",service.dashboard());m.addAttribute("activePage","dashboard");return "dashboard";}
    @GetMapping("/books") public String books(@RequestParam(required=false)String q,Model m){m.addAttribute("books",service.findBooks(q));m.addAttribute("query",q);m.addAttribute("activePage","books");return "books/list";}
    @GetMapping("/books/add") public String addBook(Model m){m.addAttribute("book",new Book());m.addAttribute("editing",false);m.addAttribute("activePage","books");return "books/form";}
    @GetMapping("/books/{id}/edit") public String editBook(@PathVariable Long id,Model m){m.addAttribute("book",service.getBook(id));m.addAttribute("editing",true);m.addAttribute("activePage","books");return "books/form";}
    @PostMapping("/books") public String saveBook(@Valid @ModelAttribute("book") Book book,BindingResult br,@RequestParam(defaultValue="false")boolean editing,Model m,RedirectAttributes ra){if(br.hasErrors()){m.addAttribute("editing",editing);m.addAttribute("activePage","books");return "books/form";}try{service.saveBook(book);ra.addFlashAttribute("success",editing?"Book updated successfully.":"Book added successfully.");return "redirect:/books";}catch(Exception e){m.addAttribute("error",friendly(e));m.addAttribute("editing",editing);m.addAttribute("activePage","books");return "books/form";}}
    @PostMapping("/books/{id}/delete") public String deleteBook(@PathVariable Long id,RedirectAttributes ra){try{service.deleteBook(id);ra.addFlashAttribute("success","Book deleted successfully.");}catch(Exception e){ra.addFlashAttribute("error",friendly(e));}return "redirect:/books";}

    @GetMapping("/members") public String members(@RequestParam(required=false)String q,Model m){m.addAttribute("members",service.findMembers(q));m.addAttribute("query",q);m.addAttribute("activePage","members");return "members/list";}
    @GetMapping("/members/register") public String registerMember(Model m){m.addAttribute("member",new Member());m.addAttribute("editing",false);m.addAttribute("activePage","members");return "members/form";}
    @GetMapping("/members/{id}/edit") public String editMember(@PathVariable Long id,Model m){m.addAttribute("member",service.getMember(id));m.addAttribute("editing",true);m.addAttribute("activePage","members");return "members/form";}
    @PostMapping("/members") public String saveMember(@Valid @ModelAttribute("member") Member member,BindingResult br,@RequestParam(defaultValue="false")boolean editing,Model m,RedirectAttributes ra){if(br.hasErrors()){m.addAttribute("editing",editing);m.addAttribute("activePage","members");return "members/form";}try{service.saveMember(member);ra.addFlashAttribute("success",editing?"Member updated successfully.":"Member registered successfully.");return "redirect:/members";}catch(Exception e){m.addAttribute("error",friendly(e));m.addAttribute("editing",editing);m.addAttribute("activePage","members");return "members/form";}}
    @PostMapping("/members/{id}/delete") public String deleteMember(@PathVariable Long id,RedirectAttributes ra){try{service.deleteMember(id);ra.addFlashAttribute("success","Member deleted successfully.");}catch(Exception e){ra.addFlashAttribute("error",friendly(e));}return "redirect:/members";}

    @GetMapping("/loans/issue") public String issue(Model m){m.addAttribute("issueDate",LocalDate.now());m.addAttribute("dueDate",LocalDate.now().plusDays(14));m.addAttribute("books",books.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE));m.addAttribute("members",members.findAll());m.addAttribute("activePage","issue");return "loans/issue";}
    @PostMapping("/loans/issue") public String issue(@RequestParam Long bookId,@RequestParam Long memberId,@RequestParam LocalDate issueDate,RedirectAttributes ra){try{service.issueBook(bookId,memberId,issueDate);ra.addFlashAttribute("success","Book issued successfully. Due date: "+issueDate.plusDays(14));}catch(Exception e){ra.addFlashAttribute("error",friendly(e));}return "redirect:/loans/issue";}
    @GetMapping("/loans/return") public String returns(Model m){m.addAttribute("loans",service.activeLoans());m.addAttribute("returnDate",LocalDate.now());m.addAttribute("activePage","return");return "loans/return";}
    @PostMapping("/loans/{id}/return") public String doReturn(@PathVariable Long id,@RequestParam LocalDate returnDate,RedirectAttributes ra){try{Loan loan=service.returnBook(id,returnDate);ra.addFlashAttribute("success","Book returned. Fine assessed: ₹"+loan.getFineAmount()+". Fine is unpaid.");}catch(Exception e){ra.addFlashAttribute("error",friendly(e));}return "redirect:/loans/return";}
    @GetMapping("/loans/history") public String history(Model m){m.addAttribute("loans",service.allLoans());m.addAttribute("activePage","history");return "loans/history";}
    @GetMapping("/loans/overdue") public String overdue(Model m){m.addAttribute("loans",service.overdueLoans());m.addAttribute("overdueDays",service);m.addAttribute("activePage","overdue");return "loans/overdue";}
    @GetMapping("/about") public String about(Model m){m.addAttribute("activePage","about");return "about";}

    private String friendly(Exception e){if(e instanceof LibraryException)return e.getMessage();return "The operation could not be completed. Please verify the data and try again.";}
}
