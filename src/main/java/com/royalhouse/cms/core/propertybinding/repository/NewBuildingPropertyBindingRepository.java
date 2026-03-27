package com.royalhouse.cms.core.propertybinding.repository;

import com.royalhouse.cms.core.propertybinding.entity.NewBuildingPropertyBinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewBuildingPropertyBindingRepository extends JpaRepository<NewBuildingPropertyBinding, Long> {
    List<NewBuildingPropertyBinding> findAllByNewBuilding_IdOrderByIdAsc(Long newBuildingId);

    Optional<NewBuildingPropertyBinding> findByProperty_Id(Long propertyId);

    boolean existsByProperty_Id(Long propertyId);

    boolean existsByNewBuilding_IdAndProperty_Id(Long newBuildingId, Long propertyId);

    void deleteByNewBuilding_IdAndProperty_Id(Long newBuildingId, Long propertyId);
}