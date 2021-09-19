package com.webapp.webapp.validator;

import com.webapp.webapp.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserDataValidator {

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    // static Pattern object, since pattern is fixed
    private static Pattern pattern;

    // non-static Matcher object because it's created from the input String
    private Matcher matcher;

    public void checkEmail(String email) {
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(email).matches()) {
            throw new BadRequestException("Invalid Email", "email");
        }
    }

    public void checkUserData(String firstName, String lastName) {
        if (firstName == null || firstName.length() == 0) {
            throw new BadRequestException("Empty First Name, Please enter a valid first name", "firstName");
        }
        if (lastName == null || lastName.length() == 0) {
            throw new BadRequestException("Empty Last Name, Please enter a valid last name", "lastName");
        }
    }

    public void checkPasswordStrength(String password){
        String pattern="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$]).{8,}$";
        if(!password.matches(pattern)) {
            throw new BadRequestException(("Password is weak. Your password minimum length should be 8."+
                    "It must consist of at least one digit 0-9." +
                    "One lowercase character [a-z]. " +
                    "One uppercase character [A-Z]." +
                    "One special character from [#?!@$]"),"password");
        }
    }
}
