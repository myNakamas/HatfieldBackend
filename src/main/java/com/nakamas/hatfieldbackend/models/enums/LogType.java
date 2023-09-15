package com.nakamas.hatfieldbackend.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogType {
    //region TICKET
    CREATED_TICKET("User %s created Ticket#%s;"),
    UPDATED_TICKET("User %s updated Ticket#%s;%s;"),
    STARTED_TICKET("User %s started Ticket#%s;"),
    FINISHED_TICKET("User %s finished Ticket#%s;"),
    COLLECTED_TICKET("The client collected their ticket from User %s; Ticket#%s;"),
    //endregion

    //region CATEGORY
    CREATED_CATEGORY("User %s created Category %s;"),
    UPDATED_CATEGORY("User %s updated Category %s;%s;"),
    DELETED_CATEGORY("User %s deleted Category %s;"),
    //endregion

    //region INVENTORY ITEM
    ADD_NEW_ITEM_TO_INVENTORY("User %s added %s of item %s to inventory;"),
    ADD_ITEM_TO_SHOPPING_LIST("User %s added item %s to the shopping list;"),
    REMOVE_ITEM_FROM_SHOPPING_LIST("User %s removed item %s from the shopping list;"),
    USED_PART("User %s used %s %s for Ticket#%s;"),
    BOUGHT_ITEM("User %s bought %s of %s;"),
    SOLD_ITEM("User %s sold %s of %s;"),
    UPDATE_ITEM_COUNT("User %s updated item %s count from %s to %s;"),
    UPDATE_ITEM("User %s updated item %s;%s;"),
    DAMAGED_PART("User %s set %s %s as DAMAGED;"),
    DEFECTIVE_PART("User %s set %s %s as DEFECTIVE;"),
    RETURNED_DEFECTIVE_PART("User %s exchanged %s defective items of type %s to new ones;"),
    //endregion

    //region INVOICE
    CREATED_SELL_INVOICE("User %s created sale Invoice#%s;"),
    CREATED_REPAIR_INVOICE("User %s created repair Invoice#%s;"),
    CREATED_BUY_INVOICE("User %s created buy Invoice#%s;"),
    CREATED_ACCESSORIES_INVOICE("User %s created accessory Invoice#%s;"),
    CREATED_DEPOSIT_INVOICE("User %s created a deposit Invoice#%s for a ticket;"),
    INVALIDATED_INVOICE("User %s invalidated Invoice#%s;"),
    //endregion

    //region USER
    CREATED_WORKER("User %s created worker %s;"),
    CREATED_CLIENT("User %s created client %s;"),
    UPDATED_USER("User %s updated user %s;%s;"),
    BANNED_USER("User %s banned user %s;"),
    UNBANNED_USER("User %s unbanned user %s;"),
    DELETED_USER("User %s deleted user %s;"),
    RESTORED_USER("User %s restored user %s;"),
    //endregion

    //region SHOP
    CREATED_SHOP("User %s created Shop %s;"),
    UPDATED_SHOP("User %s updated Shop %s;%s;");
    //endregion

    private final String message;

    public static LogType getLogType(InvoiceType invoiceType) {
        return switch (invoiceType) {
            case SELL -> CREATED_SELL_INVOICE;
            case REPAIR -> CREATED_REPAIR_INVOICE;
            case BUY -> CREATED_BUY_INVOICE;
            case ACCESSORIES -> CREATED_ACCESSORIES_INVOICE;
            case DEPOSIT -> CREATED_DEPOSIT_INVOICE;
        };
    }

}
