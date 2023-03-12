package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InventoryItemFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ItemPropertyView;
import com.nakamas.hatfieldbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final UsedPartRepository usedPartRepository;


    public InventoryItem createInventoryItem(CreateInventoryItem inventoryItem) {
        Brand brand = getOrCreateBrand(inventoryItem.brandId(), inventoryItem.brand());
        Model model = getOrCreateModel(inventoryItem.modelId(), inventoryItem.model());
        Category category = categoryRepository.findById(inventoryItem.categoryId()).orElseThrow(() -> new CustomException("Missing category"));
//        todo: Create tests for the categories
        inventoryItem.properties().entrySet().removeIf(property -> !category.getFields().contains(property.getKey()));

        InventoryItem item = new InventoryItem(
                inventoryItem,
                brand,
                model,
                shopRepository.getReferenceById(inventoryItem.shopId()),
                category
        );
        return inventoryItemRepository.save(item);
    }

    public List<CategoryView> getAllCategoryViews() {
        List<Category> all = categoryRepository.findAll();
        return all.stream().map(CategoryView::new).collect(Collectors.toList());
    }

    public void useItemForTicket(Long inventoryItemId, Long ticketId, Integer count) {
        InventoryItem item = inventoryItemRepository.findById(inventoryItemId).orElse(null);
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (item == null)
            throw new CustomException("Item does not exist!");
        if (ticket == null)
            throw new CustomException("Ticket does not exist!");
        if (item.getCount() < count)
            throw new CustomException("Not enough Items in storage!");
        item.setCount(item.getCount() - count);
        inventoryItemRepository.save(item);
        //TODO: dont forget to add the user that made the change in the log table. this is where the connection should be
        usedPartRepository.save(new UsedPart(ticket, item, count, LocalDateTime.now()));

    }

    public PageView<InventoryItemView> getShopInventory(Long shopId,InventoryItemFilter filter, PageRequestView pageRequestView) {
        filter.setShopId(shopId);
        Page<InventoryItem> items = inventoryItemRepository.findAll(filter, pageRequestView.getPageRequest());
        Page<InventoryItemView> page = items.map(InventoryItemView::new);
        return new PageView<>(page);
    }

    public List<ItemPropertyView> getAllModels() {
        return modelRepository.findAllModels();
    }

    public List<ItemPropertyView> getAllBrands() {
        return brandRepository.findAllBrands();
    }

    public void updateQuantity(Long id, Integer quantity) {
        inventoryItemRepository.updateQuantity(id, quantity);
    }

    public void remove(Long id) {
        InventoryItem item = inventoryItemRepository.getReferenceById(id);
        item.setCount(0);
        //todo: set interested boolean to false as well
        inventoryItemRepository.save(item);
    }

    public Model getOrCreateModel(Long modelId, String modelValue) {
        if (modelId != null)
            return modelRepository.findById(modelId).orElseThrow(() -> new CustomException("Model with that Id does not exist"));
        return getOrCreateModel(modelValue);
    }

    public Model getOrCreateModel(String modelValue) {
        if(modelValue==null || modelValue.isBlank()) return null;
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
        if(brandValue==null || brandValue.isBlank()) return null;
        Brand existingByName = brandRepository.findByName(brandValue);
        if (existingByName != null) return existingByName;
        return brandRepository.save(new Brand(brandValue));
    }

    public CategoryView createCategory(CategoryView createView) {
        Category save = categoryRepository.save(new Category(createView));
        return new CategoryView(save);
    }

    public CategoryView updateCategory(CategoryView categoryView, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CustomException("Cant find category by id"));
        category.update(categoryView);
        return new CategoryView(categoryRepository.save(category));
    }

    public void changeNeed(Long id, Boolean need){
        InventoryItem item = inventoryItemRepository.getReferenceById(id);
        item.setShoppingListNeeded(need);
        inventoryItemRepository.save(item);
    }
}
