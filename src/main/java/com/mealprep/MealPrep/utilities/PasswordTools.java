package com.mealprep.MealPrep.utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordTools {

    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        int saltLength = 16; // salt length in bytes
        int hashLength = 32; // hash length in bytes
        int parallelism = 1; // currently not supported by Spring Security
        int memory = 4096;   // memory costs
        int iterations = 3;
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism,
                                         memory, iterations);
        //Argon2PasswordEncoder requires implementation 'org
        // .bouncycastle:bcprov-jdk15on:1.64'
    }
}
