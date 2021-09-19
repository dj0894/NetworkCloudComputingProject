package com.webapp.webapp.repository;

import org.junit.Assert;
import com.webapp.webapp.repository.model.User;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.Optional;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindByEmail(){
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
    }



}
