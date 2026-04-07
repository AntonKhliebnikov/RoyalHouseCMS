package com.royalhouse.cms.core.serviceitem.specification;

import com.royalhouse.cms.core.serviceitem.entity.ServiceItem;
import org.springframework.data.jpa.domain.Specification;

public final class ServiceItemSpecifications {
    private ServiceItemSpecifications() {
    }

    public static Specification<ServiceItem> nameContains(String name) {
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

    public static Specification<ServiceItem> hasVisibleStatus(Boolean isVisible) {
        return (root, query, cb) -> {
            if (isVisible == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("isVisible"), isVisible);
        };
    }
}