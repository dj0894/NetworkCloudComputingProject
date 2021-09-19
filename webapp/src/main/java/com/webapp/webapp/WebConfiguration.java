package com.webapp.webapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebConfiguration extends WebSecurityConfigurerAdapter {


    @Override
    public void configure(WebSecurity web) throws Exception {
        // This Configuration is allowing all the Requests as the Basic Authentication is being
        // performed at the UserController level where it is Using the Auth Header to authenticate
        // the User.
        web.ignoring().antMatchers("/**");
    }

}