package com.webapp.webapp.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.timgroup.statsd.StatsDClient;
import com.webapp.webapp.repository.model.User;
import com.webapp.webapp.model.UserCredentials;
import com.webapp.webapp.repository.UserRepository;
import com.webapp.webapp.security.PasswordEncoder;
import com.webapp.webapp.validator.UserDataValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@WithMockUser
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    UserDataValidator userDataValidator;

    @MockBean
    StatsDClient statsDClient;

    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String USER_DATA ="{\n" +
            "    \"firstName\":\"deepika\",\n" +
            "    \"lastName\":\"jha\",\n" +
            "    \"email\": \"deepika31@mail.com\",\n" +
            "    \"password\":\"Deep@900\"\n" +
            "\n" +
            "}";


    @Test
    public void testGetUser() throws Exception {

        User user = new User();
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setEmail("admin@gmail.com");
        user.setPassword(PasswordEncoder.encodePassword("Admin@123"));
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(user));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/v1/user/self").accept(MediaType.APPLICATION_JSON)
                .headers(formAuthHeader(new UserCredentials(user.getEmail(), "Admin@123")));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String response = (result.getResponse().getContentAsString());
        User actual = OBJECT_MAPPER.readValue(response, User.class);
        assertUserData(actual, user);

    }

    @Test
    public void testCreateUser() throws Exception {
        User user=new User();
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setEmail("admin123@gmail.com");
        user.setPassword("Admin@123");

        Mockito.doNothing().when(userDataValidator).checkEmail(Mockito.anyString());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/v1/user").accept(MediaType.APPLICATION_JSON)
                .content(user.toString())
                .contentType(MediaType.APPLICATION_JSON).content(USER_DATA);


        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        int status = (result.getResponse().getStatus());
        Assert.assertEquals(200, status);
    }

    private void assertUserData(User actual, User expected) {
        Assert.assertEquals(actual.getFirstName(), expected.getFirstName());
        Assert.assertEquals(actual.getLastName(), expected.getLastName());
        Assert.assertEquals(actual.getEmail(), expected.getEmail());
        Assert.assertNull(actual.getPassword());
    }

    private HttpHeaders formAuthHeader(UserCredentials userCredentials) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String encoding = Base64.getEncoder().encodeToString((userCredentials.getEmail()
                + ":" + userCredentials.getPassword()).getBytes(StandardCharsets.UTF_8));
        responseHeaders.set(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
        return responseHeaders;
    }


}

