package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.repositories.BrandRepository;
import com.nakamas.hatfieldbackend.repositories.InventoryItemRepository;
import com.nakamas.hatfieldbackend.repositories.ModelRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final ShopRepository shopRepository;


    public InventoryItem createInventoryItem(CreateInventoryItem inventoryItem){
        InventoryItem item = new InventoryItem(
                inventoryItem,
                brandRepository.getReferenceById(inventoryItem.brandId()),
                modelRepository.getReferenceById(inventoryItem.modelId()),
                shopRepository.getReferenceById(inventoryItem.shopId())
        );
        return inventoryItemRepository.save(item);
    }

    public void useItemForTicket(Long inventoryItemId, Long ticketId, Integer count){
        //todo: predpolagam se suzdava usedItem obekt i se maha broika ot inventoryItem
    }

}
