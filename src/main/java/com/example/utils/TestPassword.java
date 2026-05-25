package com.example.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {

    public static void main(String[] args) {

        System.out.println( "This is the Password: " +
                new BCryptPasswordEncoder()
                        .encode("emp123")
        );
    }
}
