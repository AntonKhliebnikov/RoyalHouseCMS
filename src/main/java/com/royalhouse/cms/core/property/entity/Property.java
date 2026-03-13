package com.royalhouse.cms.core.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "properties")

public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type",  nullable = false,  length = 20)
    private PropertyType propertyType;

    @NotNull
    @Positive
    @Digits(integer = 8, fraction = 2)
    @Column(name = "area", nullable = false, precision = 10, scale = 2)
    private BigDecimal area;

    @NotNull
    @Positive
    @Digits(integer = 13, fraction = 2)
    @Column(name = "price",  nullable = false,  precision = 15, scale = 2)
    private BigDecimal price;

    @PositiveOrZero
    @Column(name = "rooms")
    private Integer rooms;

    @PositiveOrZero
    @Column(name = "floor")
    private Integer floor;

    @PositiveOrZero
    @Column(name = "total_floors")
    private Integer totalFloors;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at",  nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT,  EventType.UPDATE})
    @Column(name = "updated_at",   nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
}