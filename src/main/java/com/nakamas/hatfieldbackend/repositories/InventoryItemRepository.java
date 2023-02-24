package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView(i)" +
            " from InventoryItem i where i.shop.id = ?1 ")
    Page<InventoryItemView> findAllByShopId(Long shop_id, Pageable pageable);
}
