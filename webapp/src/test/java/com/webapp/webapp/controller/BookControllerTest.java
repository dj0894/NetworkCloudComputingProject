package com.webapp.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timgroup.statsd.StatsDClient;
import com.webapp.webapp.dto.Book;
import com.webapp.webapp.model.UserCredentials;
import com.webapp.webapp.publisher.SnsPublisher;
import com.webapp.webapp.repository.BookRepository;
import com.webapp.webapp.repository.ImageRepository;
import com.webapp.webapp.repository.UserRepository;
import com.webapp.webapp.repository.model.BookEntity;
import com.webapp.webapp.repository.model.User;
import com.webapp.webapp.security.PasswordEncoder;
import com.webapp.webapp.uploader.BookImageUploader;
import com.webapp.webapp.validator.UserDataValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BookController.class)

public class BookControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private BookImageUploader bookImageUploader;

    @MockBean
    private SnsPublisher snsPublisher;

    @MockBean
    StatsDClient statsDClient;

    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void name() {
    }

    @Test
    public void testGetAllBooks() throws Exception {

        List<BookEntity> allBooks = new ArrayList<BookEntity>();
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle("dummy");
        bookEntity.setId(UUID.randomUUID());
        User user = new User();
        user.setId(UUID.randomUUID());
        bookEntity.setUser(user);
        allBooks.add(bookEntity);
        Mockito.when(bookRepository.findAll()).thenReturn(allBooks);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/books")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String response = (result.getResponse().getContentAsString());
        Book[] actual = OBJECT_MAPPER.readValue(response, Book[].class);
        assertBookData(actual[0], bookEntity);
    }

    public void assertBookData(Book actual, BookEntity expected) {
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
    }

}
