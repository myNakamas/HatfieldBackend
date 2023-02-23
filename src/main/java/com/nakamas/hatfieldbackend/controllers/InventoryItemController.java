package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/inventoryItem")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @PostMapping("create")
    public InventoryItem createInventoryItem(@RequestBody CreateInventoryItem inventoryItem){
        return inventoryItemService.createInventoryItem(inventoryItem);
    }


}
