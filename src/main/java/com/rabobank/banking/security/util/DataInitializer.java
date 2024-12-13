package com.rabobank.banking.security.util;

import com.rabobank.banking.model.User;
import com.rabobank.banking.model.UserRole;
import com.rabobank.banking.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Log4j2
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initializeDatabase() {
        return args -> {
            if (userRepository.count() == 0) {
                createInitialUsers();
            }
        };
    }

    @Transactional
    public void createInitialUsers() {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminpass"))
                .role(UserRole.ROLE_ADMIN)
                .build();

        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("pass"))
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(admin);
        userRepository.save(user);

        log.info("Initial users created");
    }
}
