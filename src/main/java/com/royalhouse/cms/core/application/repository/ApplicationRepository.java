package com.royalhouse.cms.core.application.repository;

import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {
    Page<Application> findAllByStatus(ApplicationStatus status, Pageable pageable);
}
