package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InventoryItemFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ItemPropertyView;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/inventory")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @PostMapping("item/create")
    public InventoryItemView createInventoryItem(@RequestBody CreateInventoryItem inventoryItem) {
        return new InventoryItemView(inventoryItemService.createInventoryItem(inventoryItem));
    }
    @PostMapping("item/updateQuantity")
    public void updateQuantity(@RequestBody InventoryItemView inventoryItem) {
        inventoryItemService.updateQuantity(inventoryItem.id(), inventoryItem.count());
    }
    @PostMapping("item/remove")
    public void remove(@RequestBody Long id) {
        inventoryItemService.remove(id);
    }
    @GetMapping("item/all")
    public PageView<InventoryItemView> getShopInventory(@AuthenticationPrincipal User loggedUser, InventoryItemFilter filter, PageRequestView pageRequestView) {
        return inventoryItemService.getShopInventory(loggedUser.getShop().getId(), filter, pageRequestView);
    }
    @PostMapping("item/changeNeed")
    public void changeNeed(@RequestParam Long id, @RequestParam Boolean need){
        inventoryItemService.changeNeed(id, need);
    }
    @GetMapping("model/all")
    public List<ItemPropertyView> getAllModels() {
        return inventoryItemService.getAllModels();
    }
    @GetMapping("brand/all")
    public List<ItemPropertyView> getAllBrands() {
        return inventoryItemService.getAllBrands();
    }

}
