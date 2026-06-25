package com.royalhouse.cms.admin.settings.propertybinding.service;

import com.royalhouse.cms.admin.settings.propertybinding.dto.BoundPropertyRowDto;
import com.royalhouse.cms.admin.settings.propertybinding.dto.CandidatePropertyRowDto;
import com.royalhouse.cms.admin.settings.propertybinding.dto.NewBuildingBindingRowDto;
import com.royalhouse.cms.admin.settings.propertybinding.dto.PropertyBindingManagePageDto;
import com.royalhouse.cms.core.common.embeddable.Address;
import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import com.royalhouse.cms.core.newbuilding.exception.NewBuildingNotFoundException;
import com.royalhouse.cms.core.newbuilding.repository.NewBuildingRepository;
import com.royalhouse.cms.core.property.entity.Property;
import com.royalhouse.cms.core.property.entity.PropertyType;
import com.royalhouse.cms.core.property.repository.PropertyRepository;
import com.royalhouse.cms.core.propertybinding.entity.NewBuildingPropertyBinding;
import com.royalhouse.cms.core.propertybinding.exception.PropertyBindingException;
import com.royalhouse.cms.core.propertybinding.repository.NewBuildingPropertyBindingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class NewBuildingPropertyBindingService {
    private final NewBuildingRepository newBuildingRepository;
    private final PropertyRepository propertyRepository;
    private final NewBuildingPropertyBindingRepository bindingRepository;
    private static final List<PropertyType> ALLOWED_PROPERTY_TYPES = List.of(
            PropertyType.APARTMENT,
            PropertyType.COMMERCIAL
    );

    @Transactional(readOnly = true)
    public Page<NewBuildingBindingRowDto> getNewBuildingRows(String name, Pageable pageable) {
        log.info("Call method getNewBuildingRows for property bindings with name={}", name);


        return newBuildingRepository.findAllByNameFilter(normalizeSearchValue(name), pageable)
                .map(this::toNewBuildingBindingDto);
    }

    @Transactional(readOnly = true)
    public PropertyBindingManagePageDto getManagePage(Long newBuildingId) {
        log.info("Call method getManagePage for newBuildingId={}", newBuildingId);

        NewBuilding newBuilding = getNewBuildingById(newBuildingId);

        List<BoundPropertyRowDto> boundProperties = bindingRepository
                .findByNewBuildingIdWithProperty(newBuildingId)
                .stream()
                .map(binding -> toBoundPropertyRowDto(binding.getProperty()))
                .toList();

        List<CandidatePropertyRowDto> candidateProperties = findCandidateProperties(newBuilding)
                .stream()
                .map(this::toCandidatePropertyRowDto)
                .toList();

        return PropertyBindingManagePageDto.builder()
                .newBuildingId(newBuildingId)
                .newBuildingName(newBuilding.getName())
                .newBuildingAddress(formatAddress(newBuilding.getAddress()))
                .boundProperties(boundProperties)
                .candidateProperties(candidateProperties)
                .build();
    }

    public void attachProperties(Long newBuildingId, String propertyIds) {
        log.info("Call method attachProperties for newBuildingId={}", newBuildingId);

        NewBuilding newBuilding = getNewBuildingById(newBuildingId);
        List<Long> propertyIdsList = parsePropertyIds(propertyIds);

        if (propertyIdsList.isEmpty()) {
            throw new PropertyBindingException("Укажите хотя бы один ID объекта недвижимости");
        }

        List<Property> properties = propertyRepository.findAllById(propertyIdsList);

        validateAllPropertiesFound(propertyIdsList, properties);
        attachValidatedProperties(newBuilding, properties);
    }

    public void attachProperty(Long newBuildingId, Long propertyId) {
        log.info("Call method attachProperty for newBuildingId={}, propertyId={}",
                newBuildingId, propertyId);

        attachProperties(newBuildingId, String.valueOf(propertyId));
    }

    public void detachProperty(Long newBuildingId, Long propertyId) {
        log.info("Call method detachProperty for newBuildingId={}, propertyId={}",
                newBuildingId, propertyId);

        NewBuildingPropertyBinding binding = bindingRepository
                .findByNewBuildingIdAndPropertyId(newBuildingId, propertyId)
                .orElseThrow(() -> new PropertyBindingException(
                        "Связь между новостроем и объектом недвижимости не найдена"
                ));

        bindingRepository.delete(binding);
    }

    public void attachAllCandidatesByAddress(Long newBuildingId) {
        log.info("Call method attachAllCandidatesByAddress for newBuildingId={}", newBuildingId);

        NewBuilding newBuilding = getNewBuildingById(newBuildingId);

        List<Property> candidates = findCandidateProperties(newBuilding);

        attachValidatedProperties(newBuilding, candidates);
    }

    private void attachValidatedProperties(NewBuilding newBuilding, List<Property> properties) {
        Long newBuildingId = newBuilding.getId();

        for (Property property : properties) {
            validateAllowedPropertyType(property);
            validatePropertyIsNotBoundToAnotherNewBuilding(newBuildingId, property.getId());
        }

        for (Property property : properties) {
            if (bindingRepository.existsByNewBuildingIdAndPropertyId(newBuildingId, property.getId())) {
                continue;
            }

            NewBuildingPropertyBinding binding = new NewBuildingPropertyBinding();
            binding.setNewBuilding(newBuilding);
            binding.setProperty(property);

            bindingRepository.save(binding);
        }
    }

    private String normalizeSearchValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private void validatePropertyIsNotBoundToAnotherNewBuilding(Long newBuildingId, Long propertyId) {
        bindingRepository.findByPropertyId(propertyId)
                .ifPresent(existingBinding -> {
                    Long existingNewBuildingId = existingBinding.getNewBuilding().getId();

                    if (!existingNewBuildingId.equals(newBuildingId)) {
                        throw new PropertyBindingException(
                                "Объект недвижимости id=" + propertyId +
                                        " уже привязан к другому новострою id=" + existingNewBuildingId
                        );
                    }
                });
    }

    private void validateAllowedPropertyType(Property property) {
        if (!ALLOWED_PROPERTY_TYPES.contains(property.getPropertyType())) {
            throw new PropertyBindingException(
                    "К новострою можно привязывать только квартиры и коммерческую недвижимость. " +
                            "Недопустимый объект id=" + property.getId() +
                            ", тип=" + property.getPropertyType()
            );
        }
    }

    private void validateAllPropertiesFound(List<Long> requestedIds, List<Property> foundProperties) {
        Set<Long> foundIds = foundProperties.stream()
                .map(Property::getId)
                .collect(Collectors.toSet());

        List<Long> missingIds = requestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new PropertyBindingException(
                    "Не найдены объекты недвижимости с ID: " + missingIds
            );
        }
    }

    private List<Long> parsePropertyIds(String propertyIds) {
        if (propertyIds == null || propertyIds.isBlank()) {
            return List.of();
        }

        try {
            return Arrays.stream(propertyIds.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .map(Long::valueOf)
                    .distinct()
                    .toList();
        } catch (NumberFormatException e) {
            throw new PropertyBindingException(
                    "ID объектов недвижимости должны быть числами, разделёнными запятыми"
            );
        }
    }

    private List<Property> findCandidateProperties(NewBuilding newBuilding) {
        Address address = newBuilding.getAddress();

        if (address == null
                || isNullOrBlank(address.getCity())
                || isNullOrBlank(address.getDistrict())
                || isNullOrBlank(address.getStreet())
                || isNullOrBlank(address.getHouseNumber())) {
            return List.of();
        }

        return bindingRepository.findUnboundPropertiesByAddress(
                ALLOWED_PROPERTY_TYPES,
                address.getCity(),
                address.getDistrict(),
                address.getStreet(),
                address.getHouseNumber()
        );
    }

    private NewBuilding getNewBuildingById(Long newBuildingId) {
        return newBuildingRepository.findById(newBuildingId)
                .orElseThrow(() -> new NewBuildingNotFoundException(newBuildingId));
    }

    private NewBuildingBindingRowDto toNewBuildingBindingDto(NewBuilding newBuilding) {
        return NewBuildingBindingRowDto.builder()
                .newBuildingId(newBuilding.getId())
                .name(newBuilding.getName())
                .address(formatAddress(newBuilding.getAddress()))
                .boundPropertiesCount(bindingRepository.countByNewBuildingId(newBuilding.getId()))
                .build();
    }

    private BoundPropertyRowDto toBoundPropertyRowDto(Property property) {
        return BoundPropertyRowDto.builder()
                .propertyId(property.getId())
                .propertyType(property.getPropertyType())
                .area(property.getArea())
                .price(property.getPrice())
                .rooms(property.getRooms())
                .floor(property.getFloor())
                .totalFloors(property.getTotalFloors())
                .address(formatAddress(property.getAddress()))
                .build();
    }

    private CandidatePropertyRowDto toCandidatePropertyRowDto(Property property) {
        return CandidatePropertyRowDto.builder()
                .propertyId(property.getId())
                .propertyType(property.getPropertyType())
                .area(property.getArea())
                .price(property.getPrice())
                .rooms(property.getRooms())
                .floor(property.getFloor())
                .totalFloors(property.getTotalFloors())
                .address(formatAddress(property.getAddress()))
                .build();
    }

    private String formatAddress(Address address) {
        if (address == null) {
            return "----";
        }

        return String.join(", ",
                nullToDash(address.getCity()),
                nullToDash(address.getDistrict()),
                nullToDash(address.getStreet()),
                nullToDash(address.getHouseNumber())
        );
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "----" : value;
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }
}