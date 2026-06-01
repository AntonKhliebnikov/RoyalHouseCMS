package com.royalhouse.cms.admin.settings.aboutcompany.service;

import com.royalhouse.cms.admin.common.service.FileStorageService;
import com.royalhouse.cms.admin.settings.aboutcompany.dto.AboutCompanySettingsForm;
import com.royalhouse.cms.core.aboutcompany.entity.AboutCompanySettings;
import com.royalhouse.cms.core.aboutcompany.exception.AboutCompanySettingsException;
import com.royalhouse.cms.core.aboutcompany.repository.AboutCompanySettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class AboutCompanySettingsService {
    private static final Long SETTINGS_ID = 1L;
    private static final String ABOUT_COMPANY_UPLOAD_DIR = "about-company/banner";
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final AboutCompanySettingsRepository repository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public AboutCompanySettingsForm getSettingsForm() {

        log.info("Call method getSettingsForm()");

        AboutCompanySettings settings = repository.findById(SETTINGS_ID)
                .orElseGet(this::createDefaultSettings);

        return toForm(settings);
    }

    @Transactional
    public void saveSettings(AboutCompanySettingsForm form) {

        log.info("Call method saveSettings()");

        AboutCompanySettings settings = repository.findById(SETTINGS_ID)
                .orElseGet(this::createDefaultSettings);


        String oldBannerPath = settings.getBannerImagePath();
        String newBannerPath = null;

        try {
            if (hasFile(form.getBannerImage())) {
                validateImage(form.getBannerImage());
                newBannerPath = fileStorageService.store(form.getBannerImage(), ABOUT_COMPANY_UPLOAD_DIR);
                settings.setBannerImagePath(newBannerPath);
            }

            settings.setBannerText(normalize(form.getBannerText()));
            settings.setTitle(normalizeRequired(form.getTitle()));
            settings.setDescription(normalize(form.getDescription()));

            repository.saveAndFlush(settings);

            if (newBannerPath != null && StringUtils.hasText(oldBannerPath)) {
                safeDelete(oldBannerPath);
            }
        } catch (AboutCompanySettingsException e) {
            if (newBannerPath != null) {
                safeDelete(newBannerPath);
            }

            throw e;
        } catch (Exception e) {
            if (newBannerPath != null) {
                safeDelete(newBannerPath);
            }

            throw new AboutCompanySettingsException("Не удалось сохранить настройки страницы \"О компании\"", e);
        }

    }

    private void safeDelete(String path) {
        try {
            fileStorageService.delete(path);
        } catch (Exception e) {
            log.warn("Не удалось удалить файл: {}", path, e);
        }
    }

    private String normalizeRequired(String value) {
        if (!StringUtils.hasText(value)) {
            throw new AboutCompanySettingsException("Заголовок обязателен");
        }

        return value.trim();
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new AboutCompanySettingsException("Баннер должен быть изображением JPG, PNG или WEBP");
        }
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private AboutCompanySettingsForm toForm(AboutCompanySettings settings) {
        AboutCompanySettingsForm form = new AboutCompanySettingsForm();
        form.setBannerImagePath(settings.getBannerImagePath());
        form.setBannerText(settings.getBannerText());
        form.setTitle(settings.getTitle());
        form.setDescription(settings.getDescription());
        return form;
    }

    private AboutCompanySettings createDefaultSettings() {
        AboutCompanySettings settings = new AboutCompanySettings();
        settings.setId(SETTINGS_ID);
        settings.setTitle("О компании");
        return settings;
    }
}
