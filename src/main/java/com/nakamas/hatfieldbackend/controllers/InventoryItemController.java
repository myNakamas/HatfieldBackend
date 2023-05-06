package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InventoryItemFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.BrandView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.InventoryItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ItemPropertyView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ShortItemView;
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
        InventoryItem save = inventoryItemService.createInventoryItem(inventoryItem);
        return new InventoryItemView(save, inventoryItemService.getCategory(save.getCategoryId()));
    }

    @GetMapping("item")
    public InventoryItemView createInventoryItem(@RequestParam Long id) {
        return new InventoryItemView(inventoryItemService.getItem(id));
    }

    @PostMapping("item/update")
    public InventoryItemView updateInventoryItem(@RequestBody CreateInventoryItem inventoryItem) {
        InventoryItem save = inventoryItemService.updateInventoryItem(inventoryItem);
        return new InventoryItemView(save, inventoryItemService.getCategory(save.getCategoryId()));
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

    @GetMapping("item/short")
    public List<ShortItemView> getAllShopInventory(@AuthenticationPrincipal User loggedUser, InventoryItemFilter filter) {
        return inventoryItemService.getShortShopInventory(loggedUser.getShop().getId(), filter);
    }

    @GetMapping("item/required")
    public List<InventoryItemView> getShoppingList(InventoryItemFilter filter) {
        return inventoryItemService.getShoppingList(filter);
    }

    @PutMapping("item/required")
    public void getShoppingList(@RequestBody List<Long> ids, @RequestParam Boolean isNeeded) {
        inventoryItemService.changeNeed(ids, isNeeded);
    }

    @PostMapping("item/changeNeed")
    public void changeNeed(@RequestParam Long id, @RequestParam Boolean need) {
        inventoryItemService.changeNeed(id, need);
    }

    @PutMapping("item/required/count")
    public void updateRequiredItemCount(@RequestParam Long id, @RequestParam Integer count) {
        inventoryItemService.updateRequiredItemCount(id, count);
    }

    @GetMapping("model/all")
    public List<ItemPropertyView> getAllModels() {
        return inventoryItemService.getAllModels();
    }

    @GetMapping("brand/all")
    public List<BrandView> getAllBrands() {
        return inventoryItemService.getAllBrands();
    }

}
