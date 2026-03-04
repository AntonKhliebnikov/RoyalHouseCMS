package com.royalhouse.cms.core.user.repository;

import com.royalhouse.cms.core.user.entity.User;
import com.royalhouse.cms.core.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(UserRole role);

    long countByRole(UserRole role);
}