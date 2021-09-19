package com.webapp.webapp.repository.model;

import com.webapp.webapp.dto.Book;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="Book")
@EntityListeners(AuditingEntityListener.class)
public class BookEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @Column(name="title", nullable=false)
    private String title;

    @Column(name="author", nullable = false)
    private String author;

    @Column(name="isbn",nullable=false)
    private String isbn;

    @Column(name="published_date",nullable = false)
    private String publishedDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="book_created",nullable = false)
    private Date bookCreated;

    @JoinColumn(name="user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Date getBookCreated() {
        return bookCreated;
    }

    public void setBookCreated(Date bookCreated) {
        this.bookCreated = bookCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
