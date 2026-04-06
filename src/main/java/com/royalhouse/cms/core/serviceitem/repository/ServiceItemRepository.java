package com.royalhouse.cms.core.serviceitem.repository;

import com.royalhouse.cms.core.serviceitem.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceItemRepository extends
        JpaRepository<ServiceItem, Long>, JpaSpecificationExecutor<ServiceItem> {
}
