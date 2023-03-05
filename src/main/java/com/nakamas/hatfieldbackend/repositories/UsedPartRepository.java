package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsedPartRepository extends JpaRepository <UsedPart, Long> {
}
