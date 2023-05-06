package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InventoryItemFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.InventoryItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ItemPropertyView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ShortItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import com.nakamas.hatfieldbackend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final UsedPartRepository usedPartRepository;
    private final LoggerService loggerService;


    public InventoryItem createInventoryItem(CreateInventoryItem inventoryItem) {
        Brand brand = getOrCreateBrand(inventoryItem.brandId(), inventoryItem.brand());
        Model model = getOrCreateModel(inventoryItem.modelId(), inventoryItem.model());
        Optional<Category> category = Optional.empty();
        if (inventoryItem.categoryId() != null) {
            category = categoryRepository.findById(inventoryItem.categoryId());
            if (inventoryItem.properties() != null)
                category.ifPresent(value -> inventoryItem.properties().entrySet().removeIf(property -> !value.getFields().contains(property.getKey())));
        }

        InventoryItem item = new InventoryItem(
                inventoryItem,
                brand,
                model,
                shopRepository.getReferenceById(inventoryItem.shopId()),
                category.orElse(null)
        );
        InventoryItem savedItem = inventoryItemRepository.save(item);
        loggerService.itemActions(new Log(LogType.ADD_NEW_ITEM_TO_INVENTORY), savedItem, 0);
        return savedItem;
    }

    public InventoryItem updateInventoryItem(CreateInventoryItem inventoryItem) {
        InventoryItem item = inventoryItemRepository.findById(inventoryItem.id()).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        Brand brand = getOrCreateBrand(inventoryItem.brandId(), inventoryItem.brand());
        Model model = getOrCreateModel(inventoryItem.modelId(), inventoryItem.model());
        Optional<Category> category = Optional.empty();
        Optional<Shop> shop = Optional.empty();
        if (inventoryItem.categoryId() != null) {
            category = categoryRepository.findById(inventoryItem.categoryId());
            category.ifPresent(value -> inventoryItem.properties().entrySet().removeIf(property -> !value.getFields().contains(property.getKey())));
        }
        if (inventoryItem.shopId() != null)
            shop = shopRepository.findById(inventoryItem.shopId());

        item.update(inventoryItem, brand, model, shop.orElse(null), category.orElse(null));
        loggerService.itemActions(new Log(LogType.UPDATE_ITEM), item, 0);
        return inventoryItemRepository.save(item);
    }

    public List<CategoryView> getAllCategoryViews() {
        List<Category> all = categoryRepository.findAll();
        return all.stream().map(CategoryView::new).collect(Collectors.toList());
    }

    public UsedPart useItemForTicket(Long inventoryItemId, Ticket ticket, Integer count) {
        InventoryItem item = getItem(inventoryItemId);
        updateItemCount(count, item);
        inventoryItemRepository.save(item);
        UsedPart usedPart = new UsedPart(ticket, item, count, ZonedDateTime.now());
        loggerService.useItemForRepair(new Log(LogType.USED_PART), item, ticket.getId(), count);
        return usedPartRepository.save(usedPart);
    }

    private static void updateItemCount(Integer count, InventoryItem item) {
        if (item.getCount() < count)
            throw new CustomException("Not enough Items in storage!");
        item.setCount(item.getCount() - count);
    }

    public PageView<InventoryItemView> getShopInventory(Long shopId, InventoryItemFilter filter, PageRequestView pageRequestView) {
        filter.setShopId(shopId);
        Page<InventoryItem> items = inventoryItemRepository.findAll(filter, pageRequestView.getPageRequest());
        Page<InventoryItemView> page = items.map(item -> new InventoryItemView(item, getCategory(item.getCategoryId())));
        return new PageView<>(page);
    }

    public List<ShortItemView> getShortShopInventory(Long shopId, InventoryItemFilter filter) {
        filter.setShopId(shopId);
        filter.setMinCount(1);
        List<InventoryItem> all = inventoryItemRepository.findAll(filter);
        return all.stream().map(ShortItemView::new).toList();
    }

    public List<ItemPropertyView> getAllModels() {
        return modelRepository.findAllModels();
    }

    public List<ItemPropertyView> getAllBrands() {
        return brandRepository.findAllBrands();
    }

    @Transactional
    public void updateQuantity(Long id, Integer quantity) {
        InventoryItem item = inventoryItemRepository.findById(id).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        loggerService.itemActions(new Log(LogType.UPDATE_ITEM_COUNT), item, quantity);
        inventoryItemRepository.updateQuantity(id, quantity);
    }

    public void remove(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        item.setCount(0);
        item.getRequiredItem().setNeeded(false);
        loggerService.itemActions(new Log(LogType.UPDATE_ITEM_COUNT), item, 0);
        inventoryItemRepository.save(item);
    }

    public Model getOrCreateModel(Long modelId, String modelValue) {
        if (modelId != null)
            return modelRepository.findById(modelId).orElseThrow(() -> new CustomException("Model with that Id does not exist"));
        return getOrCreateModel(modelValue);
    }

    public Model getOrCreateModel(String modelValue) {
        if (modelValue == null || modelValue.isBlank()) return null;
        Model existingByName = modelRepository.findByName(modelValue);
        if (existingByName != null) return existingByName;
        return modelRepository.save(new Model(modelValue));
    }

    public Brand getOrCreateBrand(Long brandId, String brandValue) {
        if (brandId != null)
            return brandRepository.findById(brandId).orElseThrow(() -> new CustomException("Brand with that Id does not exist"));
        return getOrCreateBrand(brandValue);
    }

    public Brand getOrCreateBrand(String brandValue) {
        if (brandValue == null || brandValue.isBlank()) return null;
        Brand existingByName = brandRepository.findByName(brandValue);
        if (existingByName != null) return existingByName;
        return brandRepository.save(new Brand(brandValue));
    }

    public CategoryView createCategory(CategoryView createView) {
        Category save = categoryRepository.save(new Category(createView));
        loggerService.categoryActions(new Log(LogType.CREATED_CATEGORY), createView.name());
        return new CategoryView(save);
    }

    public CategoryView updateCategory(CategoryView categoryView, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException("Cant find category by id"));
        category.update(categoryView);
        loggerService.categoryActions(new Log(LogType.UPDATED_CATEGORY), categoryView.name());
        return new CategoryView(categoryRepository.save(category));
    }

    public List<InventoryItemView> getShoppingList(InventoryItemFilter filter) {
        filter.setIsNeeded(true);
        List<InventoryItem> needed = inventoryItemRepository.findAll(filter);
        return needed.stream().map(InventoryItemView::new).toList();
    }

    public void changeNeed(Long id, Boolean need) {
        InventoryItem item = inventoryItemRepository.findById(id).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        item.getRequiredItem().setNeeded(need);
        loggerService.itemActions(new Log(LogType.UPDATE_ITEM), item, 0);
        inventoryItemRepository.save(item);
    }

    public void changeNeed(List<Long> ids, Boolean need) {
        List<InventoryItem> allById = inventoryItemRepository.findAllById(ids);
        List<InventoryItem> modified = new ArrayList<>();
        for (InventoryItem item : allById) {
            item.getRequiredItem().setNeeded(need);
            modified.add(item);
        }
        inventoryItemRepository.saveAll(modified);
    }

    public InventoryItem getItem(Long inventoryItem) {
        return inventoryItemRepository.findById(inventoryItem).orElseThrow(() -> new CustomException("Cannot find item with selected id"));
    }

    public void deleteCategory(Long id) {
        inventoryItemRepository.setItemsToNullCategory(id);
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException("No such category exists"));
        loggerService.categoryActions(new Log(LogType.DELETED_CATEGORY), category.getName());
        categoryRepository.deleteById(id);
    }

    public CategoryView getCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId).map(CategoryView::new).orElse(null);
    }

    public void sellItem(Long id, Integer count) {
        InventoryItem item = getItem(id);
        updateItemCount(count, item);
        loggerService.itemActions(new Log(LogType.CREATED_SELL_INVOICE), item, count);
        inventoryItemRepository.save(item);
    }

    public void updateRequiredItemCount(Long id, Integer count) {
        InventoryItem item = getItem(id);
        item.getRequiredItem().setRequiredAmount(count);
        item.getRequiredItem().setCurrentCount(item.getCount());
    }
}
