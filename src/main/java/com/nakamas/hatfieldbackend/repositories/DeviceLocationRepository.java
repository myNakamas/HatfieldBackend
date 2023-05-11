package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ItemPropertyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceLocationRepository extends JpaRepository<DeviceLocation, Long> {
    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ItemPropertyView(d) from DeviceLocation d")
    List<ItemPropertyView> findAllLocations();

    @Query("from DeviceLocation d where d.location like ?1")
    DeviceLocation findByName(String location);
}
