package com.royalhouse.cms.core.contact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contact_settings")
public class ContactSettings {
    @Id
    private Long id;

    @Size(max = 32)
    @Column(name = "phone", length = 32)
    private String phone;

    @Size(max = 32)
    @Column(name = "viber_phone", length = 32)
    private String viberPhone;

    @Size(max = 64)
    @Column(name = "telegram_username", length = 64)
    private String telegramUsername;

    @Email
    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "instagram_url")
    private String instagramUrl;

    @Size(max = 255)
    @Column(name = "facebook_url")
    private String facebookUrl;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
}