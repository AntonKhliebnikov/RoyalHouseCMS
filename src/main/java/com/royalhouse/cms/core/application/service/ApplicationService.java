package com.royalhouse.cms.core.application.service;

import com.royalhouse.cms.core.application.entity.Application;
import com.royalhouse.cms.core.application.entity.ApplicationStatus;
import com.royalhouse.cms.core.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

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
        log.info("Call method getById for application with id={}", id);
        return applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Application not found: id=" + id));
    }
}