package com.royalhouse.cms.core.newbuilding.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "new_building_apartment_slides",
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_new_building_apartment_slides_building_slide",
                columnNames = {"new_building_id", "slide_number"}
        )
})
public class NewBuildingApartmentsSlide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slide_number", nullable = false)
    private Short slideNumber;

    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_building_id", nullable = false)
    private NewBuilding newBuilding;
}