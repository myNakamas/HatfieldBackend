package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/inventoryItem")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @PostMapping("create")
    public InventoryItemView createInventoryItem(@RequestBody CreateInventoryItem inventoryItem) {
        return new InventoryItemView(inventoryItemService.createInventoryItem(inventoryItem));
    }

    @GetMapping("all")
    public PageView<InventoryItemView> getShopInventory(@AuthenticationPrincipal User loggedUser, PageRequestView pageRequestView) {
        return inventoryItemService.getShopInventory(loggedUser.getShop().getId(), pageRequestView);
    }


}
