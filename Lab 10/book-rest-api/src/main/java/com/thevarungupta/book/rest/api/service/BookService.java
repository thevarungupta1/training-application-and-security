package com.thevarungupta.book.rest.api.service;

import com.thevarungupta.book.rest.api.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(int id);
    Book createBook(Book book);
    Book updateBook(int id, Book book);
    void deleteBook(int id);
}
