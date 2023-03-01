package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ItemPropertyView;
import com.nakamas.hatfieldbackend.repositories.BrandRepository;
import com.nakamas.hatfieldbackend.repositories.InventoryItemRepository;
import com.nakamas.hatfieldbackend.repositories.ModelRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void useItemForTicket(Long inventoryItemId, Long ticketId, Integer count) {
        //todo: predpolagam se suzdava usedItem obekt i se maha broika ot inventoryItem
    }

    public PageView<InventoryItemView> getShopInventory(Long shopId, PageRequestView pageRequestView) {
        Page<InventoryItemView> page = inventoryItemRepository.findAllByShopId(shopId, pageRequestView.getPageRequest());
        return new PageView<>(page);
    }

    public List<ItemPropertyView> getAllModels() {
        return modelRepository.findAllModels();
    }

    public List<ItemPropertyView> getAllBrands() {
        return brandRepository.findAllBrands();
    }
}
