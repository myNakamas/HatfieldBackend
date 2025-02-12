package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InventoryItemFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.*;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import com.nakamas.hatfieldbackend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
    private final DeviceLocationRepository deviceLocationRepository;
    private final LoggerService loggerService;


    public InventoryItem createInventoryItem(CreateInventoryItem inventoryItem) {
        Brand brand = getOrCreateBrand(inventoryItem.brandId(), inventoryItem.brand());
        Model model = getOrCreateModel(inventoryItem.modelId(), inventoryItem.model(), brand);
        if (brand !=null && !brand.getModels().contains(model)) brand.getModels().add(model);
        Optional<Category> category = Optional.empty();
        if (inventoryItem.categoryId() != null) {
            category = categoryRepository.findById(inventoryItem.categoryId());
            if (inventoryItem.properties() != null)
                category.ifPresent(value -> inventoryItem.filterProperties(value.getFields()));
        }

        InventoryItem item = new InventoryItem(
                inventoryItem,
                brand,
                model,
                shopRepository.getReferenceById(inventoryItem.shopId()),
                category.orElse(null)
        );
        InventoryItem savedItem = inventoryItemRepository.save(item);
        loggerService.createLog(new Log(LogType.ADD_NEW_ITEM_TO_INVENTORY), savedItem, savedItem.getCount());
        return savedItem;
    }

    public InventoryItem updateInventoryItem(CreateInventoryItem inventoryItem) {
        InventoryItem item = inventoryItemRepository.findById(inventoryItem.id()).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        String updateInfo = loggerService.itemUpdateCheck(item, inventoryItem);
        Brand brand = getOrCreateBrand(inventoryItem.brandId(), inventoryItem.brand());
        Model model = getOrCreateModel(inventoryItem.modelId(), inventoryItem.model(), brand);
        if (brand != null && !brand.getModels().contains(model)) brand.getModels().add(model);
        Optional<Category> category = Optional.empty();
        Optional<Shop> shop = Optional.empty();
        if (inventoryItem.categoryId() != null) {
            category = categoryRepository.findById(inventoryItem.categoryId());
            category.ifPresent(value -> inventoryItem.filterProperties(value.getFields()));
        }
        if (inventoryItem.shopId() != null)
            shop = shopRepository.findById(inventoryItem.shopId());

        item.update(inventoryItem, brand, model, shop.orElse(null), category.orElse(null));
        loggerService.createLog(new Log(LogType.UPDATE_ITEM), item.getName(), updateInfo);
        return inventoryItemRepository.save(item);
    }

    public List<CategoryView> getAllCategoryViews() {
        List<Category> all = categoryRepository.findAll();
        return all.stream().map(CategoryView::new).collect(Collectors.toList());
    }

    public void sendToShop(Long itemId, Long shopId, Integer count) {
        InventoryItem item = getItem(itemId);
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new CustomException("Shop with provided id could not be found!"));
        if (count < 1) {
            throw new CustomException("Invalid count!");
        }
        if (item.getCount() < count) {
            throw new CustomException("Not enough items in inventory!");
        }
        CreateInventoryItem itemView = new CreateInventoryItem(null, item.getName(), "", item.getPurchasePrice(), item.getSellPrice(), null, null, null, null, 0, null, null, new HashMap<>(item.getOtherProperties()));
        InventoryItem newItem = inventoryItemRepository.findDuplicateByShop(item.getBrand(), item.getModel(), item.getCategoryId(), shop).
                orElse(new InventoryItem(itemView, item.getBrand(), item.getModel(), shop, categoryRepository.findById(item.getCategoryId()).orElse(null)));
        newItem.setCount(newItem.getCount() + count);
        item.setCount(item.getCount() - count);
        inventoryItemRepository.save(newItem);
        inventoryItemRepository.save(item);
    }

    public UsedPart useItemForTicket(Long inventoryItemId, Ticket ticket, Integer count) {
        InventoryItem item = getItem(inventoryItemId);
        item.removeCount(count);
        inventoryItemRepository.save(item);
        UsedPart usedPart = new UsedPart(ticket, item, count, ZonedDateTime.now());
        loggerService.createLog(new Log(LogType.USED_PART), item, ticket.getId(), count);
        return usedPartRepository.save(usedPart);
    }

    public PageView<InventoryItemView> getShopInventory(InventoryItemFilter filter, PageRequestView pageRequestView) {
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

    public List<BrandView> getAllBrands() {
        return brandRepository.findAllBrands();
    }

    @Transactional
    public void updateQuantity(Long id, Integer quantity) {
        InventoryItem item = inventoryItemRepository.findById(id).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        loggerService.createLog(new Log(LogType.UPDATE_ITEM_COUNT), item, item.getCount(), quantity);
        inventoryItemRepository.updateQuantity(id, quantity);
    }

    public void remove(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        loggerService.createLog(new Log(LogType.UPDATE_ITEM_COUNT), item, item.getCount(), 0);
        item.setCount(0);
        item.getRequiredItem().setNeeded(false);
        inventoryItemRepository.save(item);
    }

    public Model getOrCreateModel(Long modelId, String modelValue, Brand brand) {
        if (modelId != null)
            return modelRepository.findById(modelId).orElseThrow(() -> new CustomException("Model with that Id does not exist"));
        return getOrCreateModel(modelValue, brand);
    }

    public Model getOrCreateModel(String modelValue, Brand brand) {
        if (modelValue == null || modelValue.isBlank() || brand == null) return null;
        Long brandId = brand.getId();
        Model existingByName = modelRepository.findByName(modelValue, brandId);
        if (existingByName != null) {
            if (brand.getModels().stream().noneMatch((model)-> Objects.equals(model.getId(), existingByName.getId())))
                brand.getModels().add(new Model(existingByName.getModel(), brandId));
            return existingByName;
        }
        Model save = modelRepository.save(new Model(modelValue, brandId));
        brand.getModels().add(save);
        return save;
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
        loggerService.createLog(new Log(LogType.CREATED_CATEGORY), createView.name());
        return new CategoryView(save);
    }

    public CategoryView updateCategory(CategoryView categoryView, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException("Cant find category by id"));
        String updateInfo = loggerService.categoryUpdateCheck(category, categoryView);
        category.update(categoryView);
        loggerService.createLog(new Log(LogType.UPDATED_CATEGORY), categoryView.name(), updateInfo);
        return new CategoryView(categoryRepository.save(category));
    }

    public ShoppingListView getShoppingList(InventoryItemFilter filter) {
        filter.setIsNeeded(true);
        filter.setInShoppingList(true);
        List<InventoryItem> needed = inventoryItemRepository.findAll(filter);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (InventoryItem inventoryItem : needed) {
            BigDecimal purchasePrice = inventoryItem.getPurchasePrice();
            if (purchasePrice != null)
                totalPrice = totalPrice.add(purchasePrice.multiply(new BigDecimal(inventoryItem.getMissingCount())));
        }
        return new ShoppingListView(needed.stream().map(InventoryItemView::new).toList(), totalPrice);
    }

    public void changeNeed(Long id, Boolean need) {
        InventoryItem item = inventoryItemRepository.findById(id).orElseThrow(() -> new CustomException("Item with provided id could not be found"));
        item.getRequiredItem().setNeeded(need);
        if (need) {
            loggerService.createLog(new Log(LogType.ADD_ITEM_TO_SHOPPING_LIST), item.getName());
        } else {
            loggerService.createLog(new Log(LogType.REMOVE_ITEM_FROM_SHOPPING_LIST), item.getName());
        }
        inventoryItemRepository.save(item);
    }

    public void changeNeed(List<Long> ids, Boolean need) {
        List<InventoryItem> allById = inventoryItemRepository.findAllById(ids);
        for (InventoryItem item : allById) {
            changeNeed(item.getId(), need);
        }
    }

    public InventoryItem getItem(Long inventoryItem) {
        return inventoryItemRepository.findById(inventoryItem).orElseThrow(() -> new CustomException("Cannot find item with selected id"));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException("No such category exists"));
        inventoryItemRepository.setItemsToNullCategory(id);
        loggerService.createLog(new Log(LogType.DELETED_CATEGORY), category.getName());
        categoryRepository.deleteById(id);
    }

    @Transactional
    public CategoryView getCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId).map(CategoryView::new).orElse(null);
    }

    public void buyItem(Long itemId, Integer count) {
        InventoryItem item = getItem(itemId);
        item.addCount(count);
        loggerService.createLog(new Log(LogType.BOUGHT_ITEM), item, count);
        inventoryItemRepository.save(item);
    }

    public void sellItem(Long id, Integer count) {
        InventoryItem item = getItem(id);
        item.removeCount(count);
        loggerService.createLog(new Log(LogType.SOLD_ITEM), item, count);
        inventoryItemRepository.save(item);
    }

    public void updateItemSetRequiredAmount(Long id, Integer count, Boolean isNeeded) {
        InventoryItem item = getItem(id);
        item.getRequiredItem().setRequiredAmount(count);
        item.getRequiredItem().setNeeded(Objects.requireNonNullElse(isNeeded, true));
        inventoryItemRepository.save(item);
    }

    public List<ItemPropertyView> getAllDeviceLocations() {
        return deviceLocationRepository.findAllLocations();
    }

    public void markOneAsDefective(Long itemId, int count) {
        InventoryItem item = getItem(itemId);
        item.removeCount(count);
        loggerService.createLog(new Log(LogType.DEFECTIVE_PART), item, count);
        item.getRequiredItem().addDefectiveCount(count);
        inventoryItemRepository.save(item);
    }

    public void removeDefectiveItem(Long itemId, int count) {
        InventoryItem item = getItem(itemId);
        item.getRequiredItem().removeDefectiveCount(count);
        inventoryItemRepository.save(item);
    }

    public void replaceDefectiveItem(Long itemId, int count) {
        InventoryItem item = getItem(itemId);
        item.getRequiredItem().removeDefectiveCount(count);
        item.addCount(count);
        loggerService.createLog(new Log(LogType.RETURNED_DEFECTIVE_PART), item, count);
        inventoryItemRepository.save(item);
    }

    public void markOneAsDamaged(Long itemId, int count) {
        InventoryItem item = getItem(itemId);
        item.removeCount(count);
        loggerService.createLog(new Log(LogType.DAMAGED_PART), item, count);
        inventoryItemRepository.save(item);
    }

    public void addQuantity(Long itemId, Integer count) {
        InventoryItem item = getItem(itemId);
        loggerService.createLog(new Log(LogType.UPDATE_ITEM_COUNT), item, item.getCount(), item.getCount() + count);
        item.addCount(count);
        inventoryItemRepository.save(item);
    }

    public void renameModel(Long id, String value) {
        Model model = modelRepository.findById(id).orElseThrow(() -> new CustomException("Model with that id does not exist"));
        model.setModel(value);
        modelRepository.save(model);
    }
}
