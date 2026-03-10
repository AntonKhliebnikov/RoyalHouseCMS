package com.royalhouse.cms.core.application.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "application_recipient_emails")
public class ApplicationRecipientEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email",  nullable = false)
    private String email;

    @Column(name = "is_active",  nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false,  insertable = false, updatable = false)
    private Instant createdAt;
}