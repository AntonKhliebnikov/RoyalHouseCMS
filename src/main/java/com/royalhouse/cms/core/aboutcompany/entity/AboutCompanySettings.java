package com.royalhouse.cms.core.aboutcompany.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "about_company_settings")
public class AboutCompanySettings {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "banner_image_path", length = 500)
    private String bannerImagePath;

    @Column(name = "banner_text")
    private String bannerText;

    @Column(name = "title", nullable = false)
    private String title = "";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false,  insertable = false, updatable = false)
    private Instant updatedAt;
}