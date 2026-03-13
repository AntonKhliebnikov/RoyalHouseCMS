package com.royalhouse.cms.core.property.repository;

import com.royalhouse.cms.core.property.entity.Property;
import com.royalhouse.cms.core.property.entity.PropertyType;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@NoArgsConstructor
public final class PropertySpecifications {

    public static Specification<Property> hasId(Long id) {
        return (root, criteriaQuery, criteriaBuilder) ->
                id == null ? null : criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<Property> hasPropertyType(PropertyType propertyType) {
        return (root, criteriaQuery, criteriaBuilder) ->
                propertyType == null ? null : criteriaBuilder.equal(root.get("propertyType"), propertyType);
    }

    public static Specification<Property> areaGreaterThanOrEqualTo(BigDecimal areaFrom) {
        return (root, criteriaQuery, criteriaBuilder) ->
                areaFrom == null ? null : criteriaBuilder.ge(root.get("area"), areaFrom);
    }

    public static Specification<Property> areaLessThanOrEqualTo(BigDecimal areaTo) {
        return (root, query, cb) ->
                areaTo == null ? null : cb.le(root.get("area"), areaTo);
    }

    public static Specification<Property> priceGreaterThanOrEqualTo(BigDecimal priceFrom) {
        return (root, query, cb) ->
                priceFrom == null ? null : cb.ge(root.get("price"), priceFrom);
    }

    public static Specification<Property> priceLessThanOrEqualTo(BigDecimal priceTo) {
        return (root, query, cb) ->
                priceTo == null ? null : cb.le(root.get("price"), priceTo);
    }

    public static Specification<Property> hasRooms(Integer rooms) {
        return (root, query, cb) ->
                rooms == null ? null : cb.equal(root.get("rooms"), rooms);
    }
}