package com.royalhouse.cms.admin.newbuilding.service;

import com.royalhouse.cms.admin.common.service.FileStorageService;
import com.royalhouse.cms.admin.newbuilding.dto.*;
import com.royalhouse.cms.core.common.embeddable.Address;
import com.royalhouse.cms.core.common.embeddable.GeoLocation;
import com.royalhouse.cms.core.newbuilding.entity.*;
import com.royalhouse.cms.core.newbuilding.repository.*;
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
    private final NewBuildingAboutSlideRepository newBuildingAboutSlideRepository;
    private final NewBuildingInfrastructureSlideRepository newBuildingInfrastructureSlideRepository;
    private final NewBuildingApartmentsSlideRepository newBuildingApartmentsSlideRepository;
    private final NewBuildingSpecificationBlockRepository newBuildingSpecificationBlockRepository;

    @Transactional(readOnly = true)
    public Page<NewBuilding> findAll(AdminNewBuildingFilterForm filter, Pageable pageable) {
        log.debug("Find all new buildings by filter");
        Specification<NewBuilding> specification = buildSpecification(filter);
        return newBuildingRepository.findAll(specification, pageable);
    }

    public Long createNewBuilding(AdminNewBuildingCreateForm form) {
        log.debug("Create new building");

        NewBuilding newBuilding = new NewBuilding();
        newBuilding.setName(form.getName().trim());
        newBuilding.setSortOrder(form.getSortOrder());
        newBuilding.setIsActive(form.getIsActive());

        NewBuilding saved = newBuildingRepository.saveAndFlush(newBuilding);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public NewBuilding getById(Long id) {
        log.debug("Find new building with id={}", id);

        return newBuildingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("New building with id=" + id + " not found")
        );
    }

    public void updateBasic(Long id, AdminNewBuildingBasicForm form) {
        log.debug("Update basic info for new building id={}", id);

        validateInfographics(form.getBasicInfographics());

        NewBuilding newBuilding = getById(id);
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

    @Transactional(readOnly = true)
    public AdminNewBuildingBasicForm getBasicFormById(Long id) {
        log.debug("Get form for the \"Basic\" tab by id={}", id);
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

    public void delete(Long id) {
        log.debug("Delete new building with id={}", id);

        NewBuilding newBuilding = getById(id);

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

        NewBuilding newBuilding = getById(id);
        newBuilding.setAboutDescription(form.getAboutDescription());
        newBuildingRepository.save(newBuilding);

        saveOrUpdateAboutSlide(newBuilding, (short) 1, form.getSlide1Image());
        saveOrUpdateAboutSlide(newBuilding, (short) 2, form.getSlide2Image());
        saveOrUpdateAboutSlide(newBuilding, (short) 3, form.getSlide3Image());
    }

    @Transactional(readOnly = true)
    public AdminNewBuildingAboutForm getAboutFormById(Long id) {
        log.debug("Get form for the \"About the Project\" tab by id={}", id);

        NewBuilding newBuilding = getById(id);

        AdminNewBuildingAboutForm form = new AdminNewBuildingAboutForm();
        form.setAboutDescription(newBuilding.getAboutDescription());

        List<NewBuildingAboutSlide> slides =
                newBuildingAboutSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(id);

        for (NewBuildingAboutSlide slide : slides) {
            if (slide.getSlideNumber() == 1) {
                form.setCurrentSlide1ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 2) {
                form.setCurrentSlide2ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 3) {
                form.setCurrentSlide3ImagePath(slide.getImagePath());
            }
        }
        return form;
    }

    public void updateLocation(Long id, AdminNewBuildingLocationForm form) {
        log.debug("Update location info for new building id={}", id);

        NewBuilding newBuilding = getById(id);

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

    @Transactional(readOnly = true)
    public AdminNewBuildingLocationForm getLocationFormById(Long id) {
        log.debug("Get form for the \"Location\" tab by id={}", id);

        NewBuilding newBuilding = getById(id);
        AdminNewBuildingLocationForm form = new AdminNewBuildingLocationForm();

        Address address = newBuilding.getAddress();
        if (address != null) {
            form.setCity(address.getCity());
            form.setDistrict(address.getDistrict());
            form.setStreet(address.getStreet());
            form.setHouseNumber(address.getHouseNumber());
        }

        GeoLocation geoLocation = newBuilding.getGeoLocation();
        if (geoLocation != null) {
            form.setLatitude(geoLocation.getLatitude());
            form.setLongitude(geoLocation.getLongitude());
        }

        form.setLocationDescription(newBuilding.getLocationDescription());
        return form;
    }

    @Transactional(readOnly = true)
    public AdminNewBuildingInfrastructureForm getInfrastructureFormById(Long id) {
        log.debug("Get form for the \"Infrastructure\" tab by id={}", id);

        NewBuilding newBuilding = getById(id);

        AdminNewBuildingInfrastructureForm form = new AdminNewBuildingInfrastructureForm();
        form.setInfrastructureDescription(newBuilding.getInfrastructureDescription());

        List<NewBuildingInfrastructureSlide> slides =
                newBuildingInfrastructureSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(id);

        for (NewBuildingInfrastructureSlide slide : slides) {
            if (slide.getSlideNumber() == 1) {
                form.setCurrentSlide1ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 2) {
                form.setCurrentSlide2ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 3) {
                form.setCurrentSlide3ImagePath(slide.getImagePath());
            }
        }

        form.setInfrastructureInfographics(
                newBuildingInfographicRepository
                        .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(id, NewBuildingInfographicSection.INFRASTRUCTURE)
                        .stream()
                        .map(this::mapToInfographicItemForm)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        return form;
    }

    public void updateInfrastructure(Long id, AdminNewBuildingInfrastructureForm form) {
        log.debug("Update infrastructure info for new building id={}", id);

        validateInfographics(form.getInfrastructureInfographics());

        NewBuilding newBuilding = getById(id);
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

    @Transactional(readOnly = true)
    public AdminNewBuildingApartmentsForm getApartmentsFormById(Long id) {
        log.debug("Get form for the \"Apartments\" tab by id={}", id);

        NewBuilding newBuilding = getById(id);

        AdminNewBuildingApartmentsForm form = new AdminNewBuildingApartmentsForm();
        form.setApartmentsDescription(newBuilding.getApartmentsDescription());

        List<NewBuildingApartmentsSlide> slides =
                newBuildingApartmentsSlideRepository.findAllByNewBuilding_IdOrderBySlideNumberAsc(id);

        for (NewBuildingApartmentsSlide slide : slides) {
            if (slide.getSlideNumber() == 1) {
                form.setCurrentSlide1ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 2) {
                form.setCurrentSlide2ImagePath(slide.getImagePath());
            } else if (slide.getSlideNumber() == 3) {
                form.setCurrentSlide3ImagePath(slide.getImagePath());
            }
        }

        form.setApartmentsInfographics(
                newBuildingInfographicRepository
                        .findAllByNewBuilding_IdAndSectionOrderBySortOrderAsc(id, NewBuildingInfographicSection.APARTMENTS)
                        .stream()
                        .map(this::mapToInfographicItemForm)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        return form;
    }

    public void updateApartments(Long id, AdminNewBuildingApartmentsForm form) {
        log.debug("Update apartments info for new building id={}", id);

        validateInfographics(form.getApartmentsInfographics());

        NewBuilding newBuilding = getById(id);
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

    @Transactional(readOnly = true)
    public AdminNewBuildingPanoramaForm getPanoramaFormById(Long id) {
        log.debug("Get form for the \"Panorama\" tab by id={}", id);

        NewBuilding newBuilding = getById(id);
        AdminNewBuildingPanoramaForm form = new AdminNewBuildingPanoramaForm();
        form.setCurrentPanoramaImagePath(newBuilding.getPanoramaImagePath());
        return form;
    }

    public void updatePanorama(Long id, AdminNewBuildingPanoramaForm form) {
        log.debug("Update the panorama for the new building id={}", id);
        NewBuilding newBuilding = getById(id);
        String panoramaImagePath = resolvePanoramaPath(
                id,
                form.getPanoramaImage(),
                newBuilding.getPanoramaImagePath()
        );

        newBuilding.setPanoramaImagePath(panoramaImagePath);
        newBuildingRepository.save(newBuilding);
    }

    @Transactional(readOnly = true)
    public AdminNewBuildingSpecificationForm getSpecificationFormById(Long id) {
        log.debug("Get form for the \"Specification\" tab by id={}", id);

        getById(id);

        AdminNewBuildingSpecificationForm form = new AdminNewBuildingSpecificationForm();

        List<AdminNewBuildingSpecificationBlockForm> blocks =
                newBuildingSpecificationBlockRepository
                        .findAllByNewBuilding_IdOrderBySortOrderAsc(id)
                        .stream()
                        .map(this::mapToSpecificationBlockForm)
                        .collect(Collectors.toCollection(ArrayList::new));

        form.setBlocks(blocks);
        return form;
    }

    public void updateSpecification(Long id, AdminNewBuildingSpecificationForm form) {
        log.debug("Update specification for new building id={}", id);

        NewBuilding newBuilding = getById(id);

        List<AdminNewBuildingSpecificationBlockForm> safeBlocks =
                form.getBlocks() == null ? Collections.emptyList() : form.getBlocks();

        List<AdminNewBuildingSpecificationBlockForm> normalizedBlocks = safeBlocks.stream()
                .filter(block -> !isSpecificationBlockEmpty(block))
                .toList();

        if (normalizedBlocks.isEmpty()) {
            throw new IllegalArgumentException("Добавьте хотя бы один блок спецификации");
        }

        newBuildingSpecificationBlockRepository.deleteAllByNewBuilding_Id(id);

        int order = 1;
        for (AdminNewBuildingSpecificationBlockForm blockForm : normalizedBlocks) {
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

    private AdminNewBuildingSpecificationBlockForm mapToSpecificationBlockForm(NewBuildingSpecificationBlock block) {
        AdminNewBuildingSpecificationBlockForm form = new AdminNewBuildingSpecificationBlockForm();
        form.setSortOrder(block.getSortOrder());
        form.setContent(block.getContent());
        return form;
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

    public Long countByFilters(AdminNewBuildingFilterForm filter) {
        log.debug("Call method countByFilters for new building");
        Specification<NewBuilding> specification = buildSpecification(filter);
        return newBuildingRepository.count(specification);
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

    private Specification<NewBuilding> buildSpecification(AdminNewBuildingFilterForm filter) {
        return Specification.where(NewBuildingSpecifications.nameContains(filter.getName()))
                .and(NewBuildingSpecifications.addressContains(filter.getAddress()))
                .and(NewBuildingSpecifications.hasActiveStatus(filter.getIsActive()));
    }

    private AdminNewBuildingInfographicItemForm mapToInfographicItemForm(NewBuildingInfographic infographic) {
        AdminNewBuildingInfographicItemForm form = new AdminNewBuildingInfographicItemForm();
        form.setSortOrder(infographic.getSortOrder());
        form.setDescription(infographic.getDescription());
        form.setCurrentImagePath(infographic.getImagePath());
        return form;
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