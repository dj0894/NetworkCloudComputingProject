package com.webapp.webapp.model;

public class BookSNSEvent {
    public BookSNSEvent() {
    }

    public BookSNSEvent(String bookId, String email, String eventType) {
        this.email = email;
        this.bookId = bookId;
        this.eventType = eventType;
    }

    private String bookId;
    private String email;
    private String eventType;


    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
