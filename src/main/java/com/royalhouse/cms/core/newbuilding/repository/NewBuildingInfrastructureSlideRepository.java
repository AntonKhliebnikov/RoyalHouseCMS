package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingInfrastructureSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewBuildingInfrastructureSlideRepository extends JpaRepository<NewBuildingInfrastructureSlide, Long> {
    List<NewBuildingInfrastructureSlide> findAllByNewBuilding_IdOrderBySlideNumberAsc(Long newBuildingId);

    Optional<NewBuildingInfrastructureSlide> findByNewBuilding_IdAndSlideNumber(Long newBuildingId, Short slideNumber);
}
