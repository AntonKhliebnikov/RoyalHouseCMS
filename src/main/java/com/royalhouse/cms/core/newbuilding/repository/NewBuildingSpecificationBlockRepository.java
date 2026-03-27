package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuildingSpecificationBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewBuildingSpecificationBlockRepository extends JpaRepository<NewBuildingSpecificationBlock, Long> {
    List<NewBuildingSpecificationBlock> findAllByNewBuilding_IdOrderBySortOrderAsc(Long newBuildingId);

    void deleteAllByNewBuilding_Id(Long newBuildingId);
}