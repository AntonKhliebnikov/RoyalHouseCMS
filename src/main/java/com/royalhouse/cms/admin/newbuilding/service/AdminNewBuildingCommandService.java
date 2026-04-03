package com.royalhouse.cms.admin.newbuilding.service;

import com.royalhouse.cms.admin.common.service.FileStorageService;
import com.royalhouse.cms.admin.newbuilding.dto.*;
import com.royalhouse.cms.core.common.embeddable.Address;
import com.royalhouse.cms.core.common.embeddable.GeoLocation;
import com.royalhouse.cms.core.common.exception.BusinessValidationException;
import com.royalhouse.cms.core.newbuilding.entity.*;
import com.royalhouse.cms.core.newbuilding.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
public class AdminNewBuildingCommandService {
    private final NewBuildingRepository newBuildingRepository;
    private final NewBuildingInfographicRepository newBuildingInfographicRepository;
    private final FileStorageService fileStorageService;
    private final NewBuildingAboutSlideRepository newBuildingAboutSlideRepository;
    private final NewBuildingInfrastructureSlideRepository newBuildingInfrastructureSlideRepository;
    private final NewBuildingApartmentsSlideRepository newBuildingApartmentsSlideRepository;
    private final NewBuildingSpecificationBlockRepository newBuildingSpecificationBlockRepository;
    private final AdminNewBuildingQueryService adminNewBuildingQueryService;

    public Long createNewBuilding(AdminNewBuildingCreateForm form) {
        log.debug("Create new building");

        NewBuilding newBuilding = new NewBuilding();
        newBuilding.setName(form.getName().trim());
        newBuilding.setSortOrder(form.getSortOrder());
        newBuilding.setIsActive(form.getIsActive());
        NewBuilding saved = newBuildingRepository.saveAndFlush(newBuilding);
        return saved.getId();
    }

    public void updateBasic(Long id, AdminNewBuildingBasicForm form) {
        log.debug("Update basic info for new building id={}", id);

        validateInfographics(form.getBasicInfographics());
        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        applyBasicScalarFields(newBuilding, form);
        String bannerImagePath = resolveBannerPath(
                newBuilding.getId(),
                form.getBannerImage(),
                newBuilding.getBannerImagePath()
        );

        newBuilding.setBannerImagePath(bannerImagePath);
        newBuildingRepository.save(newBuilding);
        replaceInfographics(
                newBuilding,
                form.getBasicInfographics(),
                NewBuildingInfographicSection.BASIC,
                "newbuildings/" + newBuilding.getId() + "/basic/infographics"
        );
    }

    public void delete(Long id) {
        log.debug("Delete new building with id={}", id);

        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        fileStorageService.delete(newBuilding.getBannerImagePath());
        fileStorageService.delete(newBuilding.getPanoramaImagePath());
        List<NewBuildingInfographic> infographics =
                newBuildingInfographicRepository.findAllByNewBuilding_id(id);

        for (NewBuildingInfographic infographic : infographics) {
            fileStorageService.delete(infographic.getImagePath());
        }

        List<NewBuildingAboutSlide> aboutSlides =
                newBuildingAboutSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(id);

        for (NewBuildingAboutSlide slide : aboutSlides) {
            fileStorageService.delete(slide.getImagePath());
        }

        List<NewBuildingInfrastructureSlide> infrastructureSlides =
                newBuildingInfrastructureSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(id);

        for (NewBuildingInfrastructureSlide slide : infrastructureSlides) {
            fileStorageService.delete(slide.getImagePath());
        }

        List<NewBuildingApartmentsSlide> apartmentsSlides =
                newBuildingApartmentsSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(id);

        for (NewBuildingApartmentsSlide slide : apartmentsSlides) {
            fileStorageService.delete(slide.getImagePath());
        }

        newBuildingRepository.delete(newBuilding);
    }

    public void updateAbout(Long id, AdminNewBuildingAboutForm form) {
        log.debug("Updated \"About the Project\" tab for new building with id={}", id);

        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        newBuilding.setAboutDescription(form.getAboutDescription());
        newBuildingRepository.save(newBuilding);
        saveOrUpdateAboutSlide(newBuilding, (short) 1, form.getSlide1Image());
        saveOrUpdateAboutSlide(newBuilding, (short) 2, form.getSlide2Image());
        saveOrUpdateAboutSlide(newBuilding, (short) 3, form.getSlide3Image());
    }

    public void updateLocation(Long id, AdminNewBuildingLocationForm form) {
        log.debug("Update location info for new building id={}", id);

        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);

        Address address = newBuilding.getAddress();
        if (address == null) address = new Address();
        address.setCity(form.getCity().trim());
        address.setDistrict(form.getDistrict().trim());
        address.setStreet(form.getStreet().trim());
        address.setHouseNumber(form.getHouseNumber().trim());
        newBuilding.setAddress(address);

        GeoLocation geoLocation = newBuilding.getGeoLocation();
        if (geoLocation == null) geoLocation = new GeoLocation();
        geoLocation.setLatitude(form.getLatitude());
        geoLocation.setLongitude(form.getLongitude());
        newBuilding.setGeoLocation(geoLocation);

        newBuilding.setLocationDescription(normalizeBlank(form.getLocationDescription()));
        newBuildingRepository.save(newBuilding);
    }

    public void updateInfrastructure(Long id, AdminNewBuildingInfrastructureForm form) {
        log.debug("Update infrastructure info for new building id={}", id);

        validateInfographics(form.getInfrastructureInfographics());

        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        newBuilding.setInfrastructureDescription(normalizeBlank(form.getInfrastructureDescription()));
        newBuildingRepository.save(newBuilding);

        saveOrUpdateInfrastructureSlide(newBuilding, (short) 1, form.getSlide1Image());
        saveOrUpdateInfrastructureSlide(newBuilding, (short) 2, form.getSlide2Image());
        saveOrUpdateInfrastructureSlide(newBuilding, (short) 3, form.getSlide3Image());

        replaceInfographics(
                newBuilding,
                form.getInfrastructureInfographics(),
                NewBuildingInfographicSection.INFRASTRUCTURE,
                "newbuildings/" + newBuilding.getId() + "/infrastructure/infographics"
        );
    }

    public void updateApartments(Long id, AdminNewBuildingApartmentsForm form) {
        log.debug("Update apartments info for new building id={}", id);

        validateInfographics(form.getApartmentsInfographics());

        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);
        newBuilding.setApartmentsDescription(normalizeBlank(form.getApartmentsDescription()));
        newBuildingRepository.save(newBuilding);

        saveOrUpdateApartmentsSlide(newBuilding, (short) 1, form.getSlide1Image());
        saveOrUpdateApartmentsSlide(newBuilding, (short) 2, form.getSlide2Image());
        saveOrUpdateApartmentsSlide(newBuilding, (short) 3, form.getSlide3Image());

        replaceInfographics(
                newBuilding,
                form.getApartmentsInfographics(),
                NewBuildingInfographicSection.APARTMENTS,
                "newbuildings/" + newBuilding.getId() + "/apartments/infographics"
        );
    }

    public void updatePanorama(Long id, AdminNewBuildingPanoramaForm form) {
        log.debug("Update the panorama for the new building id={}", id);

        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);

        String panoramaImagePath = resolvePanoramaPath(
                id,
                form.getPanoramaImage(),
                newBuilding.getPanoramaImagePath()
        );

        newBuilding.setPanoramaImagePath(panoramaImagePath);
        newBuildingRepository.save(newBuilding);
    }

    public void updateSpecification(Long id, AdminNewBuildingSpecificationForm form) {
        log.debug("Update specification for new building id={}", id);

        NewBuilding newBuilding = adminNewBuildingQueryService.getById(id);

        List<AdminNewBuildingSpecificationBlockForm> safeBlocks =
                form.getBlocks() == null ? Collections.emptyList() : form.getBlocks();

        boolean hasAnyFilledBlock = safeBlocks.stream()
                .anyMatch(block -> !isSpecificationBlockEmpty(block));

        if (!hasAnyFilledBlock) {
            throw new BusinessValidationException("Добавьте как минимум один блок спецификации");
        }

        boolean hasEmptyBlock = safeBlocks.stream()
                .anyMatch(this::isSpecificationBlockEmpty);

        if (hasEmptyBlock) {
            throw new BusinessValidationException("Пустые блоки спецификаций нельзя сохранить. Заполните или удалите их");
        }

        newBuildingSpecificationBlockRepository.deleteAllByNewBuilding_Id(id);
        newBuildingSpecificationBlockRepository.flush();

        int order = 1;
        for (AdminNewBuildingSpecificationBlockForm blockForm : safeBlocks) {
            String normalizedContent = normalizeSpecificationContent(blockForm.getContent());

            if (!StringUtils.hasText(normalizedContent)) {
                continue;
            }

            NewBuildingSpecificationBlock block = new NewBuildingSpecificationBlock();
            block.setNewBuilding(newBuilding);
            block.setSortOrder(order++);
            block.setContent(normalizedContent);

            newBuildingSpecificationBlockRepository.save(block);
        }
    }

    private boolean isSpecificationBlockEmpty(AdminNewBuildingSpecificationBlockForm block) {
        return !StringUtils.hasText(normalizeSpecificationContent(block.getContent()));
    }

    private String normalizeSpecificationContent(String content) {
        if (content == null) {
            return null;
        }

        String value = content.trim();

        if (value.isEmpty()) {
            return null;
        }

        if ("<p><br></p>".equals(value) || "<br>".equals(value)) {
            return null;
        }

        return value;
    }

    private void saveOrUpdateApartmentsSlide(NewBuilding newBuilding, Short slideNumber, MultipartFile image) {
        if (image == null || image.isEmpty()) return;

        NewBuildingApartmentsSlide slide = newBuildingApartmentsSlideRepository
                .findByNewBuilding_IdAndSlideNumber(newBuilding.getId(), slideNumber)
                .orElseGet(() -> {
                    NewBuildingApartmentsSlide newSlide = new NewBuildingApartmentsSlide();
                    newSlide.setNewBuilding(newBuilding);
                    newSlide.setSlideNumber(slideNumber);
                    return newSlide;
                });

        fileStorageService.delete(slide.getImagePath());

        String imagePath = fileStorageService.store(
                image,
                "newbuildings/" + newBuilding.getId() + "/apartments/slide-" + slideNumber
        );

        slide.setImagePath(imagePath);
        newBuildingApartmentsSlideRepository.save(slide);
    }

    private void saveOrUpdateInfrastructureSlide(NewBuilding newBuilding, Short slideNumber, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return;
        }

        NewBuildingInfrastructureSlide slide = newBuildingInfrastructureSlideRepository
                .findByNewBuilding_IdAndSlideNumber(newBuilding.getId(), slideNumber)
                .orElseGet(() -> {
                    NewBuildingInfrastructureSlide newSlide = new NewBuildingInfrastructureSlide();
                    newSlide.setNewBuilding(newBuilding);
                    newSlide.setSlideNumber(slideNumber);
                    return newSlide;
                });

        fileStorageService.delete(slide.getImagePath());

        String imagePath = fileStorageService.store(
                image,
                "newbuildings/" + newBuilding.getId() + "/infrastructure/slide-" + slideNumber
        );

        slide.setImagePath(imagePath);
        newBuildingInfrastructureSlideRepository.save(slide);
    }

    private void saveOrUpdateAboutSlide(NewBuilding newBuilding, Short slideNumber, MultipartFile image) {
        if (image == null || image.isEmpty()) return;
        NewBuildingAboutSlide slide = newBuildingAboutSlideRepository
                .findByNewBuilding_idAndSlideNumber(newBuilding.getId(), slideNumber)
                .orElseGet(() -> {
                    NewBuildingAboutSlide newSlide = new NewBuildingAboutSlide();
                    newSlide.setNewBuilding(newBuilding);
                    newSlide.setSlideNumber(slideNumber);
                    return newSlide;
                });

        fileStorageService.delete(slide.getImagePath());

        String imagePath = fileStorageService.store(
                image,
                "newbuildings/" + newBuilding.getId() + "/about/slide-" + slideNumber
        );

        slide.setImagePath(imagePath);
        newBuildingAboutSlideRepository.save(slide);
    }

    private void applyBasicScalarFields(NewBuilding newBuilding, AdminNewBuildingBasicForm form) {
        newBuilding.setName(form.getName().trim());
        newBuilding.setSortOrder(form.getSortOrder());
        newBuilding.setIsActive(form.getIsActive());
    }

    private String resolveBannerPath(Long newBuildingId, MultipartFile bannerImage, String currentPath) {
        if (bannerImage == null || bannerImage.isEmpty()) return normalizeBlank(currentPath);
        fileStorageService.delete(currentPath);
        return fileStorageService.store(bannerImage,
                "newbuildings/" + newBuildingId + "/basic/banner");
    }

    private String resolvePanoramaPath(Long newBuildingId, MultipartFile panoramaImage, String currentPath) {
        if (panoramaImage == null || panoramaImage.isEmpty()) return normalizeBlank(currentPath);
        fileStorageService.delete(currentPath);
        return fileStorageService.store(panoramaImage,
                "newbuildings/" + newBuildingId + "/panorama");
    }

    private void replaceInfographics(
            NewBuilding newBuilding,
            List<AdminNewBuildingInfographicItemForm> items,
            NewBuildingInfographicSection section,
            String storagePath
    ) {
        List<AdminNewBuildingInfographicItemForm> safeItems =
                items == null ? Collections.emptyList() : items;

        List<NewBuildingInfographic> existing = newBuildingInfographicRepository
                .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(newBuilding.getId(), section);

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
                section
        );

        newBuildingInfographicRepository.flush();

        for (AdminNewBuildingInfographicItemForm item : safeItems) {
            if (isInfographicItemEmpty(item)) {
                continue;
            }

            String finalImagePath = normalizeBlank(item.getCurrentImagePath());

            if (item.getImage() != null && !item.getImage().isEmpty()) {
                finalImagePath = fileStorageService.store(item.getImage(), storagePath);
            }

            NewBuildingInfographic infographic = new NewBuildingInfographic();
            infographic.setNewBuilding(newBuilding);
            infographic.setSection(section);
            infographic.setSortOrder(item.getSortOrder());
            infographic.setDescription(item.getDescription());
            infographic.setImagePath(finalImagePath);

            newBuildingInfographicRepository.save(infographic);
        }
    }

    private void validateInfographics(List<AdminNewBuildingInfographicItemForm> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        Set<Integer> usedSortOrders = new HashSet<>();

        for (AdminNewBuildingInfographicItemForm item : items) {
            if (isInfographicItemEmpty(item)) {
                continue;
            }

            if (item.getSortOrder() == null || item.getSortOrder() <= 0) {
                throw new BusinessValidationException("Пожалуйста, укажите правильный порядок сортировки элемента инфографики");
            }

            if (!usedSortOrders.add(item.getSortOrder())) {
                throw new BusinessValidationException("Порядок сортировки инфографики должен быть уникальным");
            }

            if (!StringUtils.hasText(item.getDescription())) {
                throw new BusinessValidationException("Заполните описание для инфографики с порядковым номером=" + item.getSortOrder());
            }

            boolean hasCurrentImage = StringUtils.hasText(item.getCurrentImagePath());
            boolean hasNewImage = item.getImage() != null && !item.getImage().isEmpty();

            if (!hasCurrentImage && !hasNewImage) {
                throw new BusinessValidationException("Загрузите изображение для инфографики с порядковым номером=" + item.getSortOrder());
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