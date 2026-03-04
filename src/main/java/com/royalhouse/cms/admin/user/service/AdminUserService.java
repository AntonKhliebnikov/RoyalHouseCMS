package com.royalhouse.cms.admin.user.service;

import com.royalhouse.cms.admin.user.dto.AdminUserCreateForm;
import com.royalhouse.cms.admin.user.dto.AdminUserUpdateForm;
import com.royalhouse.cms.admin.user.dto.ChangePasswordForm;
import com.royalhouse.cms.admin.user.dto.ResetPasswordForm;
import com.royalhouse.cms.core.user.entity.User;
import com.royalhouse.cms.core.user.entity.UserRole;
import com.royalhouse.cms.security.repository.RememberMeTokenDao;
import com.royalhouse.cms.core.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RememberMeTokenDao rememberMeTokenDao;

    public AdminUserService(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            RememberMeTokenDao rememberMeTokenDao) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rememberMeTokenDao = rememberMeTokenDao;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void createAdmin(AdminUserCreateForm form) {
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
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User with this id does not exist"));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with this email does not exist"));
    }

    @Transactional
    public void updateAdmin(long id, AdminUserUpdateForm form) {
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
