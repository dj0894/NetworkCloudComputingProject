package com.webapp.webapp.repository;

import com.webapp.webapp.dto.Book;
import com.webapp.webapp.repository.model.BookEntity;
import com.webapp.webapp.repository.model.User;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.html.Option;
import java.util.Optional;

@DataJpaTest
@RunWith(SpringRunner.class)
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testFindBookById(){

        //creating user and verifying
        User user=new User();
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setEmail("admin@gmail.com");
        user.setPassword("Admin123");
        userRepository.save(user);
        Optional<User> found=userRepository.findByEmail("admin@gmail.com");
        Assert.assertNotNull(found);
        Assert.assertNotNull(found.get().getEmail());
        Assert.assertNotNull(found.get().getPassword());
        Assert.assertNotNull(found.get().getFirstName());
        Assert.assertNotNull(found.get().getLastName());

        BookEntity book=new BookEntity();
        book.setTitle("Khichdi");
        book.setAuthor("Deepika");
        book.setIsbn("1234567893456");
        book.setPublishedDate("May,2021");
        book.setUser(found.get());
        bookRepository.save(book);
        Optional<BookEntity> foundBook=bookRepository.findById(book.getId());
        Assert.assertNotNull(foundBook);
        Assert.assertNotNull(foundBook.get().getTitle());
        Assert.assertNotNull(foundBook.get().getAuthor());
        Assert.assertNotNull(foundBook.get().getIsbn());
        Assert.assertNotNull(foundBook.get().getPublishedDate());
        Assert.assertNotNull(foundBook.get().getUser());
    }
}
