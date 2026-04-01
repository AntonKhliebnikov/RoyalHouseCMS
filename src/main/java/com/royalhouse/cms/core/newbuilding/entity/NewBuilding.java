package com.royalhouse.cms.core.newbuilding.entity;

import com.royalhouse.cms.core.common.embeddable.Address;
import com.royalhouse.cms.core.common.embeddable.GeoLocation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "new_buildings")
public class NewBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "banner_image_path",  length = 500)
    private String bannerImagePath;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active",  nullable = false)
    private Boolean isActive = true;

    @Embedded
    private Address address;

    @Embedded
    private GeoLocation geoLocation;

    @Column(name = "about_description", columnDefinition = "TEXT")
    private String aboutDescription;

    @Column(name = "location_description", columnDefinition = "TEXT")
    private String locationDescription;

    @Column(name = "infrastructure_description", columnDefinition = "TEXT")
    private String infrastructureDescription;

    @Column(name = "apartments_description", columnDefinition = "TEXT")
    private String apartmentDescription;

    @Column(name = "panorama_image_path",   length = 500)
    private String panoramaImagePath;

    @Column(name = "created_at",  nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at",  nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
}
