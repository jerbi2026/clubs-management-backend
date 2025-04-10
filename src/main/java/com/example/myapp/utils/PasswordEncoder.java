package com.example.myapp.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public String encode(String password) {
        System.out.println(bCryptPasswordEncoder.encode(password));
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        System.out.println(bCryptPasswordEncoder.matches(rawPassword, encodedPassword));
        System.out.println(rawPassword);
        System.out.println(encodedPassword);

        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
