package com.nakamas.hatfieldbackend.services.listeners;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryListener {

    @PreUpdate
    private void beforeUpdate(InventoryItem item) {
        item.getRequiredItem().setCurrentCount(item.getCount());
    }

}
