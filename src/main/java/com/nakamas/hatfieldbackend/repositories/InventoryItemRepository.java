package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {
    @Modifying
    @Query("update InventoryItem set count = ?2 where id = ?1")
    void updateQuantity(Long id, Integer count);

    @Modifying
    @Query("update InventoryItem set categoryId = null where categoryId = ?1")
    @Transactional
    void setItemsToNullCategory(Long categoryID);

    @Query("from InventoryItem i " +
            "where i.brand = ?1 and i.model = ?2 and i.categoryId = ?3 and i.shop = ?4")
    Optional<InventoryItem> findDuplicateByShop(Brand brand, Model model, Long categoryId, Shop shopId);
}
