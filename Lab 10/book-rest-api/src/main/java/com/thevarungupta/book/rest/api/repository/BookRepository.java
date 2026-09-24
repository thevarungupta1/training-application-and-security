package com.thevarungupta.book.rest.api.repository;

import com.thevarungupta.book.rest.api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
