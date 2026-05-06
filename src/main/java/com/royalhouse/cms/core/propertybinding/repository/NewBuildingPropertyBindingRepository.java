package com.royalhouse.cms.core.propertybinding.repository;

import com.royalhouse.cms.core.property.entity.Property;
import com.royalhouse.cms.core.property.entity.PropertyType;
import com.royalhouse.cms.core.propertybinding.entity.NewBuildingPropertyBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NewBuildingPropertyBindingRepository
        extends JpaRepository<NewBuildingPropertyBinding, Long> {

    List<NewBuildingPropertyBinding> findByNewBuildingIdOrderByPropertyIdAsc(Long newBuildingId);

    boolean existsByNewBuildingIdAndPropertyId(Long newBuildingId, Long propertyId);

    Optional<NewBuildingPropertyBinding> findByPropertyId(Long propertyId);

    Optional<NewBuildingPropertyBinding> findByNewBuildingIdAndPropertyId(Long newBuildingId, Long propertyId);

    long countByNewBuildingId(Long newBuildingId);

    @Query("""
                select b
                from NewBuildingPropertyBinding b
                join fetch b.property
                where b.newBuilding.id = :newBuildingId
                order by b.property.id asc
            """)
    List<NewBuildingPropertyBinding> findByNewBuildingIdWithProperty(
            @Param("newBuildingId") Long newBuildingId
    );

    @Query("""
                select p
                from Property p
                where p.propertyType in :allowedTypes
                  and lower(trim(p.address.city)) = lower(trim(:city))
                  and lower(trim(p.address.street)) = lower(trim(:street))
                  and lower(trim(p.address.houseNumber)) = lower(trim(:houseNumber))
                  and not exists (
                      select b.id
                      from NewBuildingPropertyBinding b
                      where b.property.id = p.id
                  )
                order by p.id asc
            """)
    List<Property> findUnboundPropertiesByAddress(
            @Param("allowedTypes") Collection<PropertyType> allowedTypes,
            @Param("city") String city,
            @Param("street") String street,
            @Param("houseNumber") String houseNumber
    );
}