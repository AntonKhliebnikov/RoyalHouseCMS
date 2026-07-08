package com.royalhouse.cms.admin.application.service;

import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import com.royalhouse.cms.core.application.repository.ApplicationRepository;
import com.royalhouse.cms.core.application.service.ApplicationService;
import com.royalhouse.cms.admin.application.specification.ApplicationSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AdminApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;

    @Transactional(readOnly = true)
    public Application getById(Long id) {
        log.info("Call method getById for application with id: {}", id);

        return applicationService.getById(id);
    }

    public void delete(Long id) {
        log.info("Call method delete for application with id: {}", id);
        applicationRepository.deleteById(id);
    }

    public void toggleStatus(Long id) {
        log.info("Call method toggleStatus for application with id: {}", id);

        Application application = applicationService.getById(id);

        if (application.getStatus() == ApplicationStatus.NEW) {
            application.setStatus(ApplicationStatus.ANSWERED);
        } else {
            application.setStatus(ApplicationStatus.NEW);
        }

        applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public Page<Application> findAll(
            String fullName,
            String phone,
            String email,
            String comment,
            ApplicationStatus status,
            Pageable pageable
    ) {
        log.info("Call method findAll for admin applications");

        Specification<Application> specification =
                ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);

        return applicationRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public long countByFilters(
            String fullName,
            String phone,
            String email,
            String comment,
            ApplicationStatus status
    ) {
        log.info("Call method countByFilters for admin applications");

        Specification<Application> specification =
                ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);

        return applicationRepository.count(specification);
    }
}
