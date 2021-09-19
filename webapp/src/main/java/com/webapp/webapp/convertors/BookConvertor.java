package com.webapp.webapp.convertors;

import com.webapp.webapp.dto.Book;
import com.webapp.webapp.dto.Image;
import com.webapp.webapp.repository.model.BookEntity;
import com.webapp.webapp.repository.model.User;

import java.util.List;
import java.util.Optional;

public class BookConvertor {

    public static BookEntity convertBookToEntity(Book book, User user) {
        BookEntity bookEntity=new BookEntity();
        bookEntity.setIsbn(book.getIsbn());
        bookEntity.setAuthor(book.getAuthor());
        bookEntity.setPublishedDate(book.getPublishedDate());
        bookEntity.setUser(user);
        bookEntity.setTitle(book.getTitle());
        return bookEntity;
    }

    public static Book convertEntityToBook(BookEntity bookEntity, List<Image> imageList) {
        Book book=new Book();
        book.setId(bookEntity.getId().toString());
        book.setAuthor(bookEntity.getAuthor());
        book.setIsbn(bookEntity.getIsbn());
        book.setTitle(bookEntity.getTitle());
        book.setUserId(bookEntity.getUser().getId().toString());
        book.setBookCreated(bookEntity.getBookCreated());
        book.setPublishedDate(bookEntity.getPublishedDate());
        book.setBookImages(imageList);
        return book;
    }
}
