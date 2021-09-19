package com.webapp.webapp.repository.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="File")
public class ImageEntity {
    @Id
    @Column(name="file_id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;

    @Column(name="file_name")
    private String fileName;

    @Column(name="s3_object_name", unique = true)
    private String s3ObjectName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    private Date createdDate;

    @JoinColumn(name="book_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BookEntity bookId;

    @JoinColumn(name="user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User userId;


    public BookEntity getBookId() {
        return bookId;
    }

    public void setBookId(BookEntity bookId) {
        this.bookId = bookId;
    }

    public String getS3ObjectName() {
        return s3ObjectName;
    }

    public void setS3ObjectName(String s3ObjectName) {
        this.s3ObjectName = s3ObjectName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UUID getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }


}
