package com.royalhouse.cms.core.newbuilding.specification;

import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import org.springframework.data.jpa.domain.Specification;


public final class NewBuildingSpecifications {

    private NewBuildingSpecifications() {
    }

    public static Specification<NewBuilding> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + name.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<NewBuilding> addressContains(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isBlank()) {
                return cb.conjunction();
            }

            String value = "%" + address.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(cb.coalesce(root.get("address").get("city"), "")), value),
                    cb.like(cb.lower(cb.coalesce(root.get("address").get("district"), "")), value),
                    cb.like(cb.lower(cb.coalesce(root.get("address").get("street"), "")), value),
                    cb.like(cb.lower(cb.coalesce(root.get("address").get("houseNumber"), "")), value)
            );
        };
    }

    public static Specification<NewBuilding> hasActiveStatus(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("isActive"), isActive);
        };
    }
}