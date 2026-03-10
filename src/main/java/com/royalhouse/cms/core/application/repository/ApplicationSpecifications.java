package com.royalhouse.cms.core.application.repository;

import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public final class ApplicationSpecifications {

    public static Specification<Application> byFilters(
            String fullName,
            String phone,
            String email,
            String comment,
            ApplicationStatus status
    ) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (fullName != null && !fullName.trim().isEmpty()) {
                String likePattern = "%" + fullName.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern));
            }

            if (phone != null && !phone.trim().isEmpty()) {
                String likePattern = "%" + phone.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern));
            }

            if (email != null && !email.trim().isEmpty()) {
                String likePattern = "%" + email.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));
            }

            if (comment != null && !comment.trim().isEmpty()) {
                String likePattern = "%" + comment.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("comment")), likePattern));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}