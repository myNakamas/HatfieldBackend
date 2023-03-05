package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ItemPropertyView;
import com.nakamas.hatfieldbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final ShopRepository shopRepository;
    private final TicketRepository ticketRepository;
    private final UsedPartRepository usedPartRepository;


    public InventoryItem createInventoryItem(CreateInventoryItem inventoryItem) {
        Brand brand = getOrCreateBrand(inventoryItem.brandId(), inventoryItem.brand());
        Model model = getOrCreateModel(inventoryItem.modelId(), inventoryItem.model());

        InventoryItem item = new InventoryItem(
                inventoryItem,
                brand,
                model,
                shopRepository.getReferenceById(inventoryItem.shopId())
        );
        return inventoryItemRepository.save(item);
    }

    public void useItemForTicket(Long inventoryItemId, Long ticketId, Integer count) {
        InventoryItem item = inventoryItemRepository.findById(inventoryItemId).orElse(null);
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);

        if(item == null)
            throw new CustomException("Item does not exist!");
        if(ticket == null)
            throw new CustomException("Ticket does not exist!");
        if(item.getCount() < count)
            throw new CustomException("Not enough Items in storage!");

        item.setCount(item.getCount() - count);
        inventoryItemRepository.save(item);

        //TODO: dont forget to add the user that made the change in the log table. this is where the connection should be
        usedPartRepository.save(new UsedPart(ticket, item, count, LocalDateTime.now()));

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

    public void updateQuantity(Long id, Integer quantity){
        inventoryItemRepository.updateQuantity(id, quantity);
    }
    public void remove(Long id){
        InventoryItem item = inventoryItemRepository.getReferenceById(id);
        item.setCount(0);
        //todo: set interested boolean to false as well
        inventoryItemRepository.save(item);
    }

    private Model getOrCreateModel(Long modelId, String modelValue) {
        if(modelId != null)
            modelRepository.findById(modelId).orElseThrow(() -> new CustomException("Model with that Id does not exist"));
        Model existingByName = modelRepository.findByName(modelValue).orElse(null);
        if(existingByName != null) return existingByName;
        return modelRepository.save(new Model(modelValue));
    }

    private Brand getOrCreateBrand(Long brandId, String brandValue) {
        return brandId != null ?
                brandRepository.findById(brandId).orElseThrow(() -> new CustomException("Brand with that Id does not exist")) :
                brandRepository.save(new Brand(brandValue));
    }
}
