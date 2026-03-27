package com.royalhouse.cms.core.newbuilding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "new_building_infographics",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_new_building_infographics_building_section_sort",
                        columnNames = {"new_building_id", "section", "sort_order"}
                )
        })
public class NewBuildingInfographic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "section",  nullable = false, length = 30)
    private NewBuildingInfographicSection section;

    @Column(name = "sort_order",  nullable = false)
    private Integer sortOrder;

    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_building_id", nullable = false)
    private NewBuilding newBuilding;
}
