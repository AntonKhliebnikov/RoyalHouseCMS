package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingApartmentsSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewBuildingApartmentsSlideRepository extends JpaRepository<NewBuildingApartmentsSlide, Long> {
    List<NewBuildingApartmentsSlide> findAllByNewBuilding_IdOrderBySlideNumberAsc(Long newBuildingId);

    Optional<NewBuildingApartmentsSlide> findByNewBuilding_IdAndSlideNumber(Long newBuildingId, Short slideNumber);
}
