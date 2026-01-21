package com.saffrongardens.saffron.config;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptCheck {
    public static void main(String[] args) {
        String newPassword = "Saffron_Group##92";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(newPassword);
        System.out.println(hash);
    }
}