package com.royalhouse.cms.admin.newbuilding.service;

import com.royalhouse.cms.admin.common.service.FileStorageService;
import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingBasicForm;
import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingCreateForm;
import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingInfographicItemForm;
import com.royalhouse.cms.admin.newbuilding.dto.AdminNewBuildingFilterForm;
import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import com.royalhouse.cms.core.newbuilding.entity.NewBuildingInfographic;
import com.royalhouse.cms.core.newbuilding.entity.NewBuildingInfographicSection;
import com.royalhouse.cms.core.newbuilding.repository.NewBuildingInfographicRepository;
import com.royalhouse.cms.core.newbuilding.repository.NewBuildingRepository;
import com.royalhouse.cms.core.newbuilding.specification.NewBuildingSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminNewBuildingService {
    private final NewBuildingRepository newBuildingRepository;
    private final NewBuildingInfographicRepository newBuildingInfographicRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public Page<NewBuilding> findAll(AdminNewBuildingFilterForm filter, Pageable pageable) {
        log.debug("Find all new buildings by filter");
        Specification<NewBuilding> specification = buildSpecification(filter);
        return newBuildingRepository.findAll(specification, pageable);
    }

    public Long createInitial(AdminNewBuildingCreateForm form) {
        log.debug("Create initial new building");

        NewBuilding newBuilding = new NewBuilding();
        newBuilding.setName(form.getName().trim());
        newBuilding.setSortOrder(form.getSortOrder());
        newBuilding.setIsActive(form.getIsActive());

        NewBuilding saved = newBuildingRepository.saveAndFlush(newBuilding);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public NewBuilding getById(Long id) {
        return newBuildingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("New building with id=" + id + " not found")
        );
    }

    public void updateBasic(Long id, AdminNewBuildingBasicForm form) {
        log.debug("Update basic info for new building id={}", id);

        validateBasicInfographics(form.getBasicInfographics());

        NewBuilding newBuilding = getById(id);
        applyBasicScalarFields(newBuilding, form);

        String bannerImagePath = resolveBannerPath(
                newBuilding.getId(),
                form.getBannerImage(),
                newBuilding.getBannerImagePath()
        );
        newBuilding.setBannerImagePath(bannerImagePath);

        newBuildingRepository.save(newBuilding);
        replaceBasicInfographics(newBuilding, form.getBasicInfographics());
    }

    @Transactional(readOnly = true)
    public AdminNewBuildingBasicForm getBasicFormById(Long id) {
        NewBuilding newBuilding = getById(id);

        AdminNewBuildingBasicForm form = new AdminNewBuildingBasicForm();
        form.setName(newBuilding.getName());
        form.setCurrentBannerImagePath(newBuilding.getBannerImagePath());
        form.setSortOrder(newBuilding.getSortOrder());
        form.setIsActive(newBuilding.getIsActive());

        List<AdminNewBuildingInfographicItemForm> items = newBuildingInfographicRepository
                .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(id, NewBuildingInfographicSection.BASIC)
                .stream()
                .map(this::mapToInfographicItemForm)
                .collect(Collectors.toCollection(ArrayList::new));

        form.setBasicInfographics(items);
        return form;
    }

    private AdminNewBuildingInfographicItemForm mapToInfographicItemForm(NewBuildingInfographic infographic) {
        AdminNewBuildingInfographicItemForm form = new AdminNewBuildingInfographicItemForm();
        form.setSortOrder(infographic.getSortOrder());
        form.setDescription(infographic.getDescription());
        form.setCurrentImagePath(infographic.getImagePath());
        return form;
    }

    private Specification<NewBuilding> buildSpecification(AdminNewBuildingFilterForm filter) {
        return Specification.where(NewBuildingSpecifications.nameContains(filter.getName()))
                .and(NewBuildingSpecifications.addressContains(filter.getAddress()))
                .and(NewBuildingSpecifications.hasActiveStatus(filter.getIsActive()));
    }

    private void applyBasicScalarFields(NewBuilding newBuilding, AdminNewBuildingBasicForm form) {
        newBuilding.setName(form.getName().trim());
        newBuilding.setSortOrder(form.getSortOrder());
        newBuilding.setIsActive(form.getIsActive());
    }

    private String resolveBannerPath(Long newBuildingId, MultipartFile bannerImage, String currentPath) {
        if (bannerImage == null || bannerImage.isEmpty()) {
            return normalizeBlank(currentPath);
        }

        fileStorageService.delete(currentPath);
        return fileStorageService.store(bannerImage,
                "newbuildings/" + newBuildingId + "/banner");
    }

    private void replaceBasicInfographics(NewBuilding newBuilding, List<AdminNewBuildingInfographicItemForm> items) {
        List<AdminNewBuildingInfographicItemForm> safeItems =
                items == null ? Collections.emptyList() : items;

        List<NewBuildingInfographic> existing = newBuildingInfographicRepository
                .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(
                        newBuilding.getId(),
                        NewBuildingInfographicSection.BASIC
                );

        Set<String> oldPaths = existing.stream()
                .map(NewBuildingInfographic::getImagePath)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        Set<String> keptPaths = safeItems.stream()
                .filter(item -> StringUtils.hasText(item.getCurrentImagePath())
                        && (item.getImage() == null || item.getImage().isEmpty()))
                .map(AdminNewBuildingInfographicItemForm::getCurrentImagePath)
                .collect(Collectors.toSet());

        for (String oldPath : oldPaths) {
            if (!keptPaths.contains(oldPath)) {
                fileStorageService.delete(oldPath);
            }
        }

        newBuildingInfographicRepository.deleteAllByNewBuilding_IdAndSection(
                newBuilding.getId(),
                NewBuildingInfographicSection.BASIC
        );

        for (AdminNewBuildingInfographicItemForm item : safeItems) {
            if (isInfographicItemEmpty(item)) {
                continue;
            }

            String finalImagePath = normalizeBlank(item.getCurrentImagePath());

            if (item.getImage() != null && !item.getImage().isEmpty()) {
                finalImagePath = fileStorageService.store(
                        item.getImage(),
                        "newbuildings/" + newBuilding.getId() + "/basic-infographics"
                );
            }

            NewBuildingInfographic infographic = new NewBuildingInfographic();
            infographic.setNewBuilding(newBuilding);
            infographic.setSection(NewBuildingInfographicSection.BASIC);
            infographic.setSortOrder(item.getSortOrder());
            infographic.setDescription(item.getDescription());
            infographic.setImagePath(finalImagePath);

            newBuildingInfographicRepository.save(infographic);
        }
    }

    private void validateBasicInfographics(List<AdminNewBuildingInfographicItemForm> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        Set<Integer> usedSortOrders = new HashSet<>();

        for (AdminNewBuildingInfographicItemForm item : items) {
            if (isInfographicItemEmpty(item)) {
                continue;
            }

            if (item.getSortOrder() == null || item.getSortOrder() <= 0) {
                throw new IllegalArgumentException("Для элемента инфографики укажите корректный порядок сортировки");
            }

            if (!usedSortOrders.add(item.getSortOrder())) {
                throw new IllegalArgumentException("Порядок сортировки инфографики должен быть уникальным");
            }

            if (!StringUtils.hasText(item.getDescription())) {
                throw new IllegalArgumentException("Для инфографики с порядком " + item.getSortOrder() + " заполните описание");
            }

            boolean hasCurrentImage = StringUtils.hasText(item.getCurrentImagePath());
            boolean hasNewImage = item.getImage() != null && !item.getImage().isEmpty();

            if (!hasCurrentImage && !hasNewImage) {
                throw new IllegalArgumentException("Для инфографики с порядком " + item.getSortOrder() + " загрузите изображение");
            }
        }
    }

    private boolean isInfographicItemEmpty(AdminNewBuildingInfographicItemForm item) {
        boolean hasDescription = StringUtils.hasText(item.getDescription());
        boolean hasCurrentImage = StringUtils.hasText(item.getCurrentImagePath());
        boolean hasNewImage = item.getImage() != null && !item.getImage().isEmpty();

        return !hasDescription && !hasCurrentImage && !hasNewImage;
    }

    private String normalizeBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}