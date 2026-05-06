package com.royalhouse.cms.core.newbuilding.repository;

import com.royalhouse.cms.core.newbuilding.entity.NewBuilding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewBuildingRepository
        extends JpaRepository<NewBuilding, Long>, JpaSpecificationExecutor<NewBuilding> {

    @Query("""
                select nb
                from NewBuilding nb
                where :name is null
                   or :name = ''
                   or lower(nb.name) like lower(concat('%', :name, '%'))
            """)
    Page<NewBuilding> findAllByNameFilter(
            @Param("name") String name,
            Pageable pageable
    );
}