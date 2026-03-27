package com.royalhouse.cms.core.propertybinding.entity;

import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import com.royalhouse.cms.core.property.entity.Property;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "new_building_property_bindings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_new_building_property_bindings_property",
                        columnNames = {"property_id"}
                ),
                @UniqueConstraint(
                        name = "uk_new_building_property_bindings_property",
                        columnNames = {"new_building_id", "property_id"}
                )
        })
public class NewBuildingPropertyBinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_building_id", nullable = false)
    private NewBuilding newBuilding;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}