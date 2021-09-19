package com.webapp.webapp.security;
import com.webapp.webapp.exception.UnauthorizedError;
import org.springframework.security.crypto.bcrypt.BCrypt;

public final class PasswordEncoder {

    public static final String SALT = BCrypt.gensalt(10);

    public static String encodePassword(String password) {
           return BCrypt.hashpw(password, SALT);
    }

    public static Boolean checkPassword(String password, String encodedPassword) {
        if (BCrypt.checkpw(password, encodedPassword)) {
            return true;
        } else {
            throw new UnauthorizedError("Credentials Username or Password did not match or Username does not exist");
        }
    }

}
