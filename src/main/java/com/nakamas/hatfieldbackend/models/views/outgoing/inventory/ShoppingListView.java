package com.nakamas.hatfieldbackend.models.views.outgoing.inventory;

import java.math.BigDecimal;
import java.util.List;

public record ShoppingListView(List<InventoryItemView> items, BigDecimal totalPrice) {
}
