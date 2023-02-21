package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<ShopSettings, Long> {
}
