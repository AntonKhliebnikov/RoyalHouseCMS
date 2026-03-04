package com.royalhouse.cms.auth.service;

import com.royalhouse.cms.core.user.entity.User;
import com.royalhouse.cms.core.user.entity.UserRole;
import com.royalhouse.cms.core.user.repository.UserRepository;
import com.royalhouse.cms.auth.dto.RegistrationForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registrationFirstAdmin(RegistrationForm form) {
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            throw new IllegalStateException("Registration is closed. Admin already exists.");
        }

        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalStateException("Email already in use.");
        }

        String encodePassword = passwordEncoder.encode(form.getPassword());
        User admin = new User(
                form.getName(),
                form.getEmail(),
                encodePassword,
                UserRole.ADMIN
        );

        userRepository.save(admin);
    }
}