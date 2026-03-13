package com.royalhouse.cms.admin.user.service;

import com.royalhouse.cms.admin.user.dto.AdminUserCreateForm;
import com.royalhouse.cms.admin.user.dto.AdminUserUpdateForm;
import com.royalhouse.cms.admin.user.dto.ChangePasswordForm;
import com.royalhouse.cms.admin.user.dto.ResetPasswordForm;
import com.royalhouse.cms.core.user.entity.User;
import com.royalhouse.cms.core.user.entity.UserRole;
import com.royalhouse.cms.security.repository.RememberMeTokenDao;
import com.royalhouse.cms.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RememberMeTokenDao rememberMeTokenDao;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.info("Call method findAll for users");
        return userRepository.findAll();
    }

    @Transactional
    public void createAdmin(AdminUserCreateForm form) {
        log.info("Call method createAdmin for users");
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User(
                form.getName(),
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                UserRole.ADMIN
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(long id) {
        log.info("Call method getById for user with id {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User with this id does not exist"));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        log.info("Call method getByEmail for user with email {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with this email does not exist"));
    }

    @Transactional
    public void updateAdmin(long id, AdminUserUpdateForm form) {
        log.info("Call method updateAdmin for user with id {}", id);
        User user = getById(id);
        String oldEmail = user.getEmail();
        String newEmail = form.getEmail();

        if (!oldEmail.equals(newEmail) && userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }

        user.setName(form.getName());
        user.setEmail(form.getEmail());
        userRepository.save(user);

        if (!oldEmail.equals(newEmail)) {
            rememberMeTokenDao.deleteByUsername(oldEmail);
        }
    }

    @Transactional
    public void changeOwnPassword(String currentEmail, ChangePasswordForm form) {
        log.info("Call method changeOwnPassword for user with email {}", currentEmail);
        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            throw new IllegalStateException("New passwords do not match");
        }

        User user = getByEmail(currentEmail);
        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Old passwords is incorrect");
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
        rememberMeTokenDao.deleteByUsername(currentEmail);
    }

    @Transactional
    public void resetPassword(long targetUserId, ResetPasswordForm form, String currentEmail) {
        log.info("Call method resetPassword for user with id {}", targetUserId);
        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            throw new IllegalStateException("New passwords do not match");
        }

        User targetUser = getById(targetUserId);
        if (targetUser.getEmail().equals(currentEmail)) {
            throw new IllegalStateException("Use Change Password for your own account");
        }

        targetUser.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(targetUser);
        rememberMeTokenDao.deleteByUsername(targetUser.getEmail());
    }

    @Transactional
    public void deleteAdmin(long id, String currentUserEmail) {
        log.info("Call method deleteAdmin for user with id {}", id);
        User user = getById(id);
        if (user.getEmail().equals(currentUserEmail)) {
            throw new IllegalStateException("You cannot delete yourself");
        }

        long admins = userRepository.countByRole(UserRole.ADMIN);
        if (user.getRole() == UserRole.ADMIN && admins <= 1) {
            throw new IllegalStateException("You cannot delete the last ADMIN");
        }

        userRepository.delete(user);
    }
}
