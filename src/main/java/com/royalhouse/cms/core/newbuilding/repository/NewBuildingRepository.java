package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewBuildingRepository
        extends JpaRepository<NewBuilding, Long>, JpaSpecificationExecutor<NewBuilding> {
}
