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
@Table(
        name = "new_building_specification_blocks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_new_building_specification_blocks_building_sort",
                        columnNames = {"new_building_id", "sort_order"}
                )
        }
)
public class NewBuildingSpecificationBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_building_id", nullable = false)
    private NewBuilding newBuilding;
}