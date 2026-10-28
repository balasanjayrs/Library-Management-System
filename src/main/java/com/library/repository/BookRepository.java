package com.library.repository;
import com.library.model.Book;
import com.library.model.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BookRepository extends JpaRepository<Book,Long> {
  long countByAvailabilityStatus(AvailabilityStatus status);
  List<Book> findByAvailabilityStatus(AvailabilityStatus status);
  List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title,String author);
}
