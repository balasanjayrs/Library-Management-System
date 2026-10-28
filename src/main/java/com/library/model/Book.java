package com.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity @Table(name="books")
public class Book {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable=false)
    private String title;
    @NotBlank @Column(nullable=false)
    private String author;
    @NotBlank @Column(nullable=false)
    private String category;
    private String isbn;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getTitle(){return title;} public void setTitle(String title){this.title=title;} public String getAuthor(){return author;} public void setAuthor(String author){this.author=author;} public String getCategory(){return category;} public void setCategory(String category){this.category=category;} public String getIsbn(){return isbn;} public void setIsbn(String isbn){this.isbn=isbn;} public AvailabilityStatus getAvailabilityStatus(){return availabilityStatus;} public void setAvailabilityStatus(AvailabilityStatus availabilityStatus){this.availabilityStatus=availabilityStatus;}
}
