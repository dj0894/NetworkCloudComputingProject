package com.webapp.webapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Book {

    private String id;
    private String title; // request
    private String author; // request
    private String isbn; // request
    private String publishedDate; // request
    private Date bookCreated;
    private String userId;

    public List<Image> getBookImages() {
        return bookImages;
    }

    public void setBookImages(List<Image> bookImages) {
        this.bookImages = bookImages;
    }

    private List<Image> bookImages;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @JsonProperty("published_date")
    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    @JsonProperty("book_created")
    public Date getBookCreated() {
        return bookCreated;
    }


    public void setBookCreated(Date bookCreated) {
        this.bookCreated = bookCreated;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
