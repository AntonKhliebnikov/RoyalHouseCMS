package com.royalhouse.cms.core.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false,  length = 50)
    private String name;

    @Column(name = "email", unique = true, nullable = false , length = 100)
    private String email;

    @Column(name = "password", nullable = false,  length = 100)
    private String password;

    @Column(name = "role", nullable = false,   length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}