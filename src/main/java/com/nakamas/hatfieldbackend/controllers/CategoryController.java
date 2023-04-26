package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/category")
public class CategoryController {

    private final InventoryItemService inventoryItemService;

    @GetMapping("all")
    public List<CategoryView> getAllCategories() {
        return inventoryItemService.getAllCategoryViews();
    }

    @PostMapping("admin/create")
    public CategoryView createCategory(@RequestBody CategoryView createView) {
        return inventoryItemService.createCategory(createView);
    }

    @DeleteMapping("admin/delete")
    public void deleteCategory(@RequestParam Long id, @AuthenticationPrincipal User user) {
        inventoryItemService.deleteCategory(id, user);
    }

    @PutMapping("admin/update")
    public CategoryView updateCategory(@RequestBody CategoryView categoryView, @RequestParam Long id) {
        return inventoryItemService.updateCategory(categoryView, id);
    }
}
