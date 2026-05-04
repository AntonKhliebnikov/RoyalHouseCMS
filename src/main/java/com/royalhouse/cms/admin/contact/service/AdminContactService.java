package com.royalhouse.cms.admin.contact.service;

import com.royalhouse.cms.admin.contact.dto.AdminContactsPageForm;
import com.royalhouse.cms.admin.contact.dto.AdminRecipientEmailItemForm;
import com.royalhouse.cms.core.application.entity.ApplicationRecipientEmail;
import com.royalhouse.cms.core.application.exception.ApplicationRecipientEmailNotFoundException;
import com.royalhouse.cms.core.application.repository.ApplicationRecipientEmailRepository;
import com.royalhouse.cms.core.contact.entity.ContactSettings;
import com.royalhouse.cms.core.contact.exception.ContactSettingsNotFoundException;
import com.royalhouse.cms.core.contact.repository.ContactSettingsRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminContactService {
    private static final Long CONTACT_SETTINGS_ID = 1L;
    private final ContactSettingsRepository contactSettingsRepository;
    private final ApplicationRecipientEmailRepository applicationRecipientEmailRepository;

    @Transactional(readOnly = true)
    public AdminContactsPageForm getContactsPageForm() {
        log.debug("Load contacts page form");

        ContactSettings settings = getSettingsEntity();

        AdminContactsPageForm form = new AdminContactsPageForm();
        form.setPhone(settings.getPhone());
        form.setViberPhone(settings.getViberPhone());
        form.setTelegramUsername(settings.getTelegramUsername());
        form.setEmail(settings.getEmail());
        form.setInstagramUrl(settings.getInstagramUrl());
        form.setFacebookUrl(settings.getFacebookUrl());
        form.setAddress(settings.getAddress());

        List<ApplicationRecipientEmail> recipientEmails = applicationRecipientEmailRepository.findAll(
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        List<AdminRecipientEmailItemForm> recipientEmailForms = getAdminRecipientEmailItemForms(recipientEmails);

        form.setRecipientEmails(recipientEmailForms);
        return form;
    }

    public void saveContactsPage(AdminContactsPageForm form) {
        log.debug("Save contacts page");

        updateContactSettings(form);
        processRecipientEmails(form.getRecipientEmails());
    }

    private void updateContactSettings(AdminContactsPageForm form) {
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

    private void processRecipientEmails(@Valid List<AdminRecipientEmailItemForm> items) {
        if (items == null) {
            return;
        }

        validateDuplicateEmails(items);

        for (AdminRecipientEmailItemForm item : items) {
            processRecipientEmailItem(item);
        }
    }

    private void validateDuplicateEmails(@Valid List<AdminRecipientEmailItemForm> items) {
        Set<String> seen = new HashSet<>();

        for (AdminRecipientEmailItemForm item : items) {
            if (item == null) {
                continue;
            }

            if (Boolean.TRUE.equals(item.getMarkedForDelete())) {
                continue;
            }

            String normalizedEmail = normalizeEmail(item.getEmail());
            if (normalizedEmail == null) {
                continue;
            }

            if (!seen.add(normalizedEmail)) {
                throw new IllegalStateException("В форме есть дублирующиеся email получателей");
            }
        }
    }

    private void processRecipientEmailItem(AdminRecipientEmailItemForm item) {
        if (item == null) {
            return;
        }

        Long id = item.getId();
        String normalizedEmail = normalizeEmail(item.getEmail());
        boolean markedForDelete = Boolean.TRUE.equals(item.getMarkedForDelete());
        boolean isActive = !Boolean.FALSE.equals(item.getIsActive());

        if (id == null) {
            handleNewRecipientEmail(normalizedEmail, isActive, markedForDelete);
            return;
        }

        ApplicationRecipientEmail recipientEmail = getApplicationRecipientEmail(id);

        if (markedForDelete) {
            if (Boolean.TRUE.equals(recipientEmail.getIsActive())) {
                throw new IllegalStateException("Нельзя удалить активный email получателя");
            }

            applicationRecipientEmailRepository.delete(recipientEmail);
            return;
        }

        if (normalizedEmail == null) {
            throw new IllegalStateException("Email получателя не может быть пустым");
        }

        if (!recipientEmail.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new IllegalStateException(
                    "Редактирование существующего email не поддерживается. Удалите старый и добавьте новый."
            );
        }

        recipientEmail.setIsActive(isActive);
        applicationRecipientEmailRepository.save(recipientEmail);


    }

    private void handleNewRecipientEmail(String normalizedEmail, boolean isActive, boolean markedForDelete) {
        if (markedForDelete || normalizedEmail == null) {
            return;
        }

        ApplicationRecipientEmail existing = applicationRecipientEmailRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElse(null);

        if (existing != null) {
            if (Boolean.TRUE.equals(existing.getIsActive()) && isActive) {
                throw new IllegalStateException("Этот email уже добавлен в список получателей");
            }

            existing.setEmail(normalizedEmail);
            existing.setIsActive(isActive);
            applicationRecipientEmailRepository.save(existing);
            return;
        }

        ApplicationRecipientEmail recipientEmail = ApplicationRecipientEmail.builder()
                .email(normalizedEmail)
                .isActive(isActive)
                .build();

        applicationRecipientEmailRepository.save(recipientEmail);
    }


    private @NonNull List<AdminRecipientEmailItemForm> getAdminRecipientEmailItemForms(List<ApplicationRecipientEmail> recipientEmails) {
        List<AdminRecipientEmailItemForm> recipientEmailForms = new ArrayList<>();

        for (ApplicationRecipientEmail recipientEmail : recipientEmails) {
            AdminRecipientEmailItemForm itemForm = new AdminRecipientEmailItemForm();
            itemForm.setId(recipientEmail.getId());
            itemForm.setEmail(recipientEmail.getEmail());
            itemForm.setIsActive(recipientEmail.getIsActive());
            itemForm.setMarkedForDelete(false);
            recipientEmailForms.add(itemForm);
        }
        return recipientEmailForms;
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