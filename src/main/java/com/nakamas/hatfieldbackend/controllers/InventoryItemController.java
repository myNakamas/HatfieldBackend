package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InventoryItemFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.*;
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
    public InventoryItemView getById(@RequestParam Long id) {
        InventoryItem save = inventoryItemService.getItem(id);
        return new InventoryItemView(save, inventoryItemService.getCategory(save.getCategoryId()));
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
    public ShoppingListView getShoppingList(InventoryItemFilter filter) {
        return inventoryItemService.getShoppingList(filter);
    }

    @PutMapping("item/changeNeed")
    public void changeNeed(@RequestBody List<Long> ids, @RequestParam Boolean need) {
        inventoryItemService.changeNeed(ids, need);
    }

    @PatchMapping("item/changeNeed")
    public void changeNeed(@RequestParam Long id, @RequestParam Boolean need) {
        inventoryItemService.changeNeed(id, need);
    }

    @PatchMapping("item/mark/defective")
    public void markAsDefective(@RequestParam Long itemId,
                                @RequestParam(required = false, defaultValue = "1") Integer count) {
        inventoryItemService.markOneAsDefective(itemId, count);
    }

    @PatchMapping("item/mark/defective/replace")
    public void replaceDefectiveItem(@RequestParam Long itemId, @RequestParam(required = false, defaultValue = "1") Integer count) {
        inventoryItemService.replaceDefectiveItem(itemId, count);
    }

    @PatchMapping("item/mark/damaged")
    public void markAsDamaged(@RequestParam Long itemId, @RequestParam(required = false, defaultValue = "1") Integer count) {
        inventoryItemService.markOneAsDamaged(itemId, count);
    }

    @PostMapping("item/sendToShop")
    public void sendToShop(@RequestParam Long itemId, @RequestParam Long shopId, @RequestParam Integer count) {
        inventoryItemService.sendToShop(itemId, shopId, count);
    }

    @PatchMapping("item/required")
    public void updateRequiredItemCount(@RequestParam Long id, @RequestParam Integer count, @RequestParam(required = false) Boolean isNeeded) {
        inventoryItemService.updateItemSetRequiredAmount(id, count, isNeeded);
    }

    @GetMapping("model/all")
    public List<ItemPropertyView> getAllModels() {
        return inventoryItemService.getAllModels();
    }

    @GetMapping("brand/all")
    public List<BrandView> getAllBrands() {
        return inventoryItemService.getAllBrands();
    }

    @GetMapping("location/all")
    public List<ItemPropertyView> getAllDeviceLocations() {
        return inventoryItemService.getAllDeviceLocations();
    }

}
