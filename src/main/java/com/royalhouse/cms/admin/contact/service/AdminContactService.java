package com.royalhouse.cms.admin.contact.service;

import com.royalhouse.cms.admin.contact.dto.AdminContactSettingsForm;
import com.royalhouse.cms.admin.contact.dto.AdminRecipientEmailForm;
import com.royalhouse.cms.core.application.entity.ApplicationRecipientEmail;
import com.royalhouse.cms.core.application.exception.ApplicationRecipientEmailNotFoundException;
import com.royalhouse.cms.core.application.repository.ApplicationRecipientEmailRepository;
import com.royalhouse.cms.core.contact.entity.ContactSettings;
import com.royalhouse.cms.core.contact.exception.ContactSettingsNotFoundException;
import com.royalhouse.cms.core.contact.repository.ContactSettingsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminContactService {
    private static final Long CONTACT_SETTINGS_ID = 1L;
    private final ContactSettingsRepository contactSettingsRepository;
    private final ApplicationRecipientEmailRepository applicationRecipientEmailRepository;

    @Transactional(readOnly = true)
    public AdminContactSettingsForm getSettingsForm() {
        log.debug("Load contact settings form");

        ContactSettings settings = getSettingsEntity();
        AdminContactSettingsForm form = new AdminContactSettingsForm();
        form.setPhone(settings.getPhone());
        form.setViberPhone(settings.getViberPhone());
        form.setTelegramUsername(settings.getTelegramUsername());
        form.setEmail(settings.getEmail());
        form.setInstagramUrl(settings.getInstagramUrl());
        form.setFacebookUrl(settings.getFacebookUrl());
        form.setAddress(settings.getAddress());

        return form;
    }

    @Transactional(readOnly = true)
    public List<ApplicationRecipientEmail> getAllRecipientEmails() {
        log.debug("Load active application recipient emails");

        return applicationRecipientEmailRepository.findAll(
                Sort.by(Sort.Direction.ASC, "CreatedAt")
        );
    }

    public void updateSettings(AdminContactSettingsForm form) {
        log.debug("Update contact settings");

        ContactSettings settings = getSettingsEntity();

        settings.setPhone(normalizeNullable(form.getPhone()));
        settings.setViberPhone(normalizeNullable(form.getViberPhone()));
        settings.setTelegramUsername(normalizeNullable(form.getTelegramUsername()));
        settings.setEmail(normalizeEmail(form.getEmail()));
        settings.setInstagramUrl(normalizeNullable(form.getInstagramUrl()));
        settings.setFacebookUrl(normalizeNullable(form.getFacebookUrl()));
        settings.setAddress(normalizeNullable(form.getAddress()));

        contactSettingsRepository.save(settings);
    }

    public void addRecipientEmail(AdminRecipientEmailForm form) {
        String normalizedEmail = normalizeEmail(form.getEmail());
        log.debug("Add application recipient email={}", normalizedEmail);

        ApplicationRecipientEmail existing = applicationRecipientEmailRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElse(null);

        if (existing != null) {
            if (Boolean.TRUE.equals(existing.getIsActive())) {
                throw new IllegalStateException("Этот email уже добавлен в список получателей");
            }

            existing.setEmail(normalizedEmail);
            existing.setIsActive(true);
            applicationRecipientEmailRepository.save(existing);
            return;
        }

        ApplicationRecipientEmail recipientEmail = ApplicationRecipientEmail.builder()
                .email(normalizedEmail)
                .isActive(true)
                .build();

        applicationRecipientEmailRepository.save(recipientEmail);
    }

    public void disableRecipientEmail(Long id) {
        log.debug("Disable application recipient email id={}", id);

        ApplicationRecipientEmail recipientEmail = getApplicationRecipientEmail(id);

        if (!Boolean.TRUE.equals(recipientEmail.getIsActive())) {
            throw new IllegalStateException("Email получателя уже отключен");
        }

        recipientEmail.setIsActive(false);
        applicationRecipientEmailRepository.save(recipientEmail);
    }

    public void enableRecipientEmail(Long id) {
        log.debug("Enable application recipient email id={}", id);

        ApplicationRecipientEmail recipientEmail = getApplicationRecipientEmail(id);

        if (Boolean.TRUE.equals(recipientEmail.getIsActive())) {
            throw new IllegalStateException("Email получателя уже включен");
        }

        recipientEmail.setIsActive(true);
        applicationRecipientEmailRepository.save(recipientEmail);
    }

    public void deleteRecipientEmail(Long id) {
        log.debug("Delete application recipient email id={}", id);

        ApplicationRecipientEmail recipientEmail = getApplicationRecipientEmail(id);

        if (Boolean.TRUE.equals(recipientEmail.getIsActive())) {
            throw new IllegalStateException("Нельзя удалить активный email получателя");
        }

        applicationRecipientEmailRepository.delete(recipientEmail);
    }

    private @NonNull ApplicationRecipientEmail getApplicationRecipientEmail(Long id) {
        return applicationRecipientEmailRepository.findById(id)
                .orElseThrow(
                        () -> new ApplicationRecipientEmailNotFoundException("Email получателя не найден")
                );
    }

    private ContactSettings getSettingsEntity() {
        return contactSettingsRepository.findById(CONTACT_SETTINGS_ID)
                .orElseThrow(ContactSettingsNotFoundException::new);
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeEmail(String value) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            return null;
        }
        return normalized.toLowerCase();
    }
}