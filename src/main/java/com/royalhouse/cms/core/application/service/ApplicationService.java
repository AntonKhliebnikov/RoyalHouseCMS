package com.royalhouse.cms.core.application.service;

import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationRecipientEmail;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import com.royalhouse.cms.core.application.repository.ApplicationRecipientEmailRepository;
import com.royalhouse.cms.core.application.repository.ApplicationRepository;
import com.royalhouse.cms.core.application.repository.ApplicationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationRecipientEmailRepository applicationRecipientEmailRepository;

    public Application create(String fullName, String phone, String email, String comment) {
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
    public Page<Application> findAll(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Application> findByStatus(ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findAllByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Application getById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: id" + id));
    }

    public void delete(Long id) {
        applicationRepository.deleteById(id);
    }

    public Application toggleStatus(Long id) {
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
        return applicationRecipientEmailRepository.findAllByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Page<Application> search(
            String fullName,
            String phone,
            String email,
            String comment,
            ApplicationStatus status,
            Pageable pageable
    ) {
        Specification<Application> specification
                = ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);

        return applicationRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public List<Application> findAllForExport(
            String fullName,
            String phone,
            String email,
            String comment,
            ApplicationStatus status
    ) {
        Specification<Application> specification =
                ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);
        return applicationRepository.findAll(
                specification,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    @Transactional(readOnly = true)
    public long countByFilters(
            String fullName,
            String phone,
            String email,
            String comment,
            ApplicationStatus status
    ) {
        Specification<Application> specification =
                ApplicationSpecifications.byFilters(fullName, phone, email, comment, status);
        return applicationRepository.count(specification);
    }
}