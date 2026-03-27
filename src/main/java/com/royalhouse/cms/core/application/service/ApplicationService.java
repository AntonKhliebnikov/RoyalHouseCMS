package com.royalhouse.cms.core.application.service;

import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationRecipientEmail;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import com.royalhouse.cms.core.application.repository.ApplicationRecipientEmailRepository;
import com.royalhouse.cms.core.application.repository.ApplicationRepository;
import com.royalhouse.cms.core.application.specification.ApplicationSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationRecipientEmailRepository applicationRecipientEmailRepository;

    public Application create(String fullName, String phone, String email, String comment) {
        log.info("Call method create for application");
        Application application = Application.builder()
                .fullName(fullName)
                .phone(phone)
                .email(email)
                .comment(comment)
                .status(ApplicationStatus.NEW)
                .build();
        return applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public Application getById(Long id) {
        log.info("Call method getById for application with id: {}", id);
        return applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Application not found: id" + id));
    }

    public void delete(Long id) {
        log.info("Call method delete for application with id: {}", id);
        applicationRepository.deleteById(id);
    }

    public Application toggleStatus(Long id) {
        log.info("Call method toggleStatus for application with id: {}", id);
        Application application = getById(id);
        if (application.getStatus() == ApplicationStatus.NEW) {
            application.setStatus(ApplicationStatus.ANSWERED);
        } else {
            application.setStatus(ApplicationStatus.NEW);
        }

        return applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationRecipientEmail> getActiveRecipients() {
        log.info("Call method getActiveRecipients for application");
        return applicationRecipientEmailRepository.findAllByIsActiveTrue();
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
        log.info("Call method findAll for application");
        Specification<Application> specification
                = ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);

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
        log.info("Call method countByFilters for application");
        Specification<Application> specification =
                ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);
        return applicationRepository.count(specification);
    }
}