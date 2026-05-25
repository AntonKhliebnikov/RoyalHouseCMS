package com.royalhouse.cms.admin.settings.secondarymarket.service;

import com.royalhouse.cms.admin.common.service.FileStorageService;
import com.royalhouse.cms.admin.settings.secondarymarket.dto.SecondaryMarketSettingsForm;
import com.royalhouse.cms.admin.settings.secondarymarket.dto.SecondaryMarketSlideForm;
import com.royalhouse.cms.core.secondarymarket.entity.SecondaryMarketSlide;
import com.royalhouse.cms.core.secondarymarket.exception.SecondaryMarketSlideException;
import com.royalhouse.cms.core.secondarymarket.repository.SecondaryMarketSlideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SecondaryMarketSlideService {
    private static final String SECONDARY_MARKET_UPLOAD_DIR = "secondary-market/slides";
    private final SecondaryMarketSlideRepository slideRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public SecondaryMarketSettingsForm getSettingsForm() {
        log.info("Call method: getSettingsForm");

        List<SecondaryMarketSlide> slides = slideRepository.findAllByOrderBySortOrderAscIdAsc();

        SecondaryMarketSettingsForm form = new SecondaryMarketSettingsForm();

        List<SecondaryMarketSlideForm> slideForms = slides.stream()
                .map(this::toForm)
                .toList();

        form.setSlides(new ArrayList<>(slideForms));
        return form;
    }

    @Transactional
    public void saveSettings(SecondaryMarketSettingsForm form) {
        if (form.getSlides() == null || form.getSlides().isEmpty()) {
            log.info("Secondary market settings saved. Slides count=0");
            return;
        }

        int sortOrder = 0;

        for (SecondaryMarketSlideForm slideForm : form.getSlides()) {
            if (Boolean.TRUE.equals(slideForm.getMarkedForDelete())) {
                deleteSlide(slideForm);
                continue;
            }

            if (slideForm.getId() == null) {
                createSlide(slideForm, sortOrder);
            } else {
                updateSlide(slideForm, sortOrder);
            }

            sortOrder++;
        }

        log.info("Secondary market settings saved. Slides count={}", form.getSlides().size());
    }

    private void createSlide(SecondaryMarketSlideForm form, int sortOrder) {
        validateImageRequired(form.getImage());
        validateImageType(form.getImage());

        String imagePath = storeSlideImage(form.getImage());

        SecondaryMarketSlide slide = SecondaryMarketSlide.builder()
                .imagePath(imagePath)
                .text(normalizeNullableText(form.getText()))
                .linkUrl(normalizeLinkUrl(form.getLinkUrl()))
                .sortOrder(sortOrder)
                .isActive(Boolean.TRUE.equals(form.getIsActive()))
                .build();

        slideRepository.save(slide);
    }

    private void updateSlide(SecondaryMarketSlideForm form, int sortOrder) {
        SecondaryMarketSlide slide = slideRepository.findById(form.getId())
                .orElseThrow(() -> new SecondaryMarketSlideException("Слайд не найден"));

        MultipartFile newImage = form.getImage();

        if (newImage != null && !newImage.isEmpty()) {
            validateImageType(newImage);

            fileStorageService.delete(slide.getImagePath());

            String newImagePath = storeSlideImage(newImage);

            slide.setImagePath(newImagePath);
        }

        slide.setText(normalizeNullableText(form.getText()));
        slide.setLinkUrl(normalizeLinkUrl(form.getLinkUrl()));
        slide.setSortOrder(sortOrder);
        slide.setActive(Boolean.TRUE.equals(form.getIsActive()));
    }

    private String storeSlideImage(MultipartFile image) {
        try {
            return fileStorageService.store(image, SECONDARY_MARKET_UPLOAD_DIR);
        } catch (IllegalStateException e) {
            log.error("Failed to store secondary market slide image", e);
            throw new SecondaryMarketSlideException("Не удалось сохранить изображение слайда");
        }
    }

    private void deleteSlide(SecondaryMarketSlideForm form) {
        if (form.getId() == null) {
            return;
        }

        SecondaryMarketSlide slide = slideRepository.findById(form.getId())
                .orElseThrow(() -> new SecondaryMarketSlideException("Слайд для удаления не найден"));

        if (slide.isActive()) {
            throw new SecondaryMarketSlideException("Удалять можно только неактивные слайды");
        }

        fileStorageService.delete(slide.getImagePath());
        slideRepository.delete(slide);
    }

    private SecondaryMarketSlideForm toForm(SecondaryMarketSlide slide) {
        SecondaryMarketSlideForm form = new SecondaryMarketSlideForm();
        form.setId(slide.getId());
        form.setImagePath(slide.getImagePath());
        form.setText(slide.getText());
        form.setLinkUrl(slide.getLinkUrl());
        form.setSortOrder(slide.getSortOrder());
        form.setIsActive(slide.isActive());
        form.setMarkedForDelete(false);
        return form;
    }

    private void validateImageRequired(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new SecondaryMarketSlideException("Изображение слайда обязательно");
        }
    }

    private void validateImageType(MultipartFile image) {
        String contentType = image.getContentType();

        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            return;
        }

        if ("image/png".equalsIgnoreCase(contentType)) {
            return;
        }

        if ("image/webp".equalsIgnoreCase(contentType)) {
            return;
        }

        throw new SecondaryMarketSlideException("Разрешены только изображения JPG, PNG или WEBP");
    }

    private String normalizeNullableText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }

    private String normalizeLinkUrl(String linkUrl) {
        if (!StringUtils.hasText(linkUrl)) {
            return null;
        }

        String normalizedLinkUrl = linkUrl.trim();

        boolean relativeUrl = normalizedLinkUrl.startsWith("/");
        boolean absoluteHttpUrl = normalizedLinkUrl.startsWith("http://")
                || normalizedLinkUrl.startsWith("https://");

        if (!relativeUrl && !absoluteHttpUrl) {
            throw new SecondaryMarketSlideException(
                    "Ссылка должна начинаться с /, http:// или https://"
            );
        }

        return normalizedLinkUrl;
    }
}
