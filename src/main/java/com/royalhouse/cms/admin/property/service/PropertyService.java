package com.royalhouse.cms.admin.property.service;

import com.royalhouse.cms.admin.property.dto.AdminPropertyCreateOrUpdateForm;
import com.royalhouse.cms.admin.property.dto.AdminPropertyFilterForm;
import com.royalhouse.cms.core.property.entity.Property;
import com.royalhouse.cms.core.property.repository.PropertyRepository;
import com.royalhouse.cms.core.property.specification.PropertySpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public Property create(AdminPropertyCreateOrUpdateForm form) {
        log.info("Call method create for property");
        Property property = Property.builder()
                .propertyType(form.getPropertyType())
                .area(form.getArea())
                .price(form.getPrice())
                .rooms(form.getRooms())
                .floor(form.getFloor())
                .totalFloors(form.getTotalFloors())
                .build();

        return propertyRepository.save(property);
    }

    @Transactional(readOnly = true)
    public Page<Property> findAll(AdminPropertyFilterForm filter, Pageable pageable) {
        log.info("Call method findAll for property");
        return propertyRepository.findAll(buildSpecification(filter), pageable);
    }

    @Transactional(readOnly = true)
    public Property getById(Long id) {
        log.info("Call method getById for property with id={}", id);
        return propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Property not found for id: " + id));
    }

    public Property update(Long id, AdminPropertyCreateOrUpdateForm form) {
        log.info("Call method update for property with id={}", id);
        Property property = getById(id);
        property.setPropertyType(form.getPropertyType());
        property.setArea(form.getArea());
        property.setPrice(form.getPrice());
        property.setRooms(form.getRooms());
        property.setFloor(form.getFloor());
        property.setTotalFloors(form.getTotalFloors());
        return propertyRepository.save(property);
    }

    public void delete(Long id) {
        log.info("Call method delete for property with id={}", id);
        Property property = getById(id);
        propertyRepository.delete(property);
    }

    @Transactional(readOnly = true)
    public long countByFilters(AdminPropertyFilterForm filter) {
        log.info("Call method countByFilters for property");
        return propertyRepository.count(buildSpecification(filter));
    }

    private Specification<Property> buildSpecification(AdminPropertyFilterForm filter) {
        return Specification.where(PropertySpecifications.hasId(filter.getId()))
                .and(PropertySpecifications.hasPropertyType(filter.getPropertyType()))
                .and(PropertySpecifications.areaGreaterThanOrEqualTo(filter.getAreaFrom()))
                .and(PropertySpecifications.areaLessThanOrEqualTo(filter.getAreaTo()))
                .and(PropertySpecifications.priceGreaterThanOrEqualTo(filter.getPriceFrom()))
                .and(PropertySpecifications.priceLessThanOrEqualTo(filter.getPriceTo()))
                .and(PropertySpecifications.hasRooms(filter.getRooms()));
    }
}