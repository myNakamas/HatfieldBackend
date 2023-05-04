package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.LogFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.LogView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import com.nakamas.hatfieldbackend.repositories.LogRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoggerService {
    private final LogRepository logRepository;
    private final UserRepository userRepository;

    private String saveUser(Log logMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) return "";
        User loggedUser = (User) authentication.getPrincipal();
        logMessage.setUserId(loggedUser.getId());
        logMessage.setShopId(loggedUser.getShop().getId());
        return loggedUser.getFullName();
    }

    private void createLog(Log logMessage) {
        logRepository.save(logMessage);
    }

    public PageView<LogView> getLogs(LogFilter filter, PageRequest pageRequest) {
        Page<Log> all = logRepository.findAll(filter, pageRequest);
        return new PageView<>(all.map((log) -> new LogView(log, getUserProfile(log.getUserId()))));
    }

    private UserProfile getUserProfile(UUID id) {
        return userRepository.findById(id).map(UserProfile::new).orElse(null);
    }
    public void categoryActions(Log logMessage, String categoryName) {
        String loggedUserName = saveUser(logMessage);
        logMessage.setAction(switch (logMessage.getLogType()) {
            case CREATED_CATEGORY -> "User '%s' created Category '%s'.".formatted(loggedUserName, categoryName);
            case UPDATED_CATEGORY -> "User '%s' updated Category '%s'.".formatted(loggedUserName, categoryName);
            case DELETED_CATEGORY -> "User '%s' deleted Category '%s'.".formatted(loggedUserName, categoryName);
            default -> "User '%s' did an unknown action!".formatted(loggedUserName);
        });
        createLog(logMessage);
    }
    public void useItemForRepair(Log logMessage, InventoryItem item, Long ticketId, Integer count){
        String loggedUserName = saveUser(logMessage);
        logMessage.setTicketId(ticketId);
        logMessage.setItemId(item.getId());
        if (logMessage.getLogType() == LogType.USED_PART){
            logMessage.setAction("User '%s' used '%s' '%s' for Ticket#'%s'.".formatted(loggedUserName, count, item.getName(), ticketId));
        }else{
            logMessage.setAction("User '%s' did an unknown action!".formatted(loggedUserName));
        }
        createLog(logMessage);
    }
    public void itemActions(Log logMessage, InventoryItem item, Integer count) {
        String loggedUserName = saveUser(logMessage);
        logMessage.setItemId(item.getId());
        logMessage.setAction(switch (logMessage.getLogType()) {
            case ADD_NEW_ITEM_TO_INVENTORY ->
                    "User '%s' added '%s' of item '%s' to inventory.".formatted(loggedUserName, item.getCount(), item.getName());
            case ADD_ITEM_TO_SHOPPING_LIST ->//not implemented yet
                    "User '%s' added item '%s' to the shopping list.".formatted(loggedUserName, item.getName());
            case UPDATE_ITEM_COUNT ->
                    "User '%s' updated item '%s' count from '%s' to '%s'.".formatted(loggedUserName, item.getName(), item.getCount(), count);
            case UPDATE_ITEM ->
                    "User '%s' updated item '%s'.".formatted(loggedUserName, item.getName());
            case SCRAPPED_PART ->// not implemented yet
                    "User '%s' scrapped '%s' '%s'.".formatted(loggedUserName, count, item.getName());
            case REMOVE_ITEM_FROM_SHOPPING_LIST ->//not implemented yet
                    "User '%s' removed item '%s' from the shopping list.".formatted(loggedUserName, item.getName());
            default -> "User '%s' did an unknown action!".formatted(loggedUserName);
        });
        createLog(logMessage);
    }
    public void ticketActions(Log logMessage, Ticket ticket) {
        String loggedUserName = saveUser(logMessage);
        logMessage.setTicketId(ticket.getId());
        logMessage.setAction(switch (logMessage.getLogType()) {
            case CREATED_TICKET ->
                    "User '%s' created Ticket#'%s'.".formatted(loggedUserName, ticket.getId());
            case MOVED_TICKET ->
                    "User '%s' moved Ticket#'%s' to '%s'.".formatted(loggedUserName, ticket.getId(), ticket.getDeviceLocationString());
            case UPDATED_TICKET ->
                    "User '%s' updated Ticket#'%s'.".formatted(loggedUserName, ticket.getId());
            case STARTED_TICKET ->
                    "User '%s' started Ticket#'%s'.".formatted(loggedUserName, ticket.getId());
            case FINISHED_TICKET ->
                    "User '%s' finished Ticket#'%s'.".formatted(loggedUserName, ticket.getId());
            case COLLECTED_TICKET ->
                    "The client collected Ticket#'%s' from User '%s'.".formatted( ticket.getId(), loggedUserName);
            default -> "User '%s' did an unknown action!".formatted(loggedUserName);
        });
        createLog(logMessage);
    }
    public void shopActions(Log logMessage, Shop shop) {
        String loggedUserName = saveUser(logMessage);
        logMessage.setAction(switch (logMessage.getLogType()) {
            case CREATED_SHOP
                     -> "User '%s' created Shop '%s'.".formatted(loggedUserName, shop.getShopName());
            case UPDATED_SHOP ->
                    "User '%s' updated Shop '%s'.".formatted(loggedUserName, shop.getShopName());
            default -> "User '%s' did an unknown action!".formatted(loggedUserName);
        });
        createLog(logMessage);
    }
    public void userActions(Log logMessage, String user) {
        String loggedUserName = saveUser(logMessage);
        logMessage.setAction(switch (logMessage.getLogType()) {
            case CREATED_WORKER ->
                    "User '%s' created worker '%s'.".formatted(loggedUserName, user);
            case CREATED_CLIENT ->
                    "User '%s' created client '%s'.".formatted(loggedUserName, user);
            case UPDATED_USER ->
                    "User '%s' updated user '%s'.".formatted(loggedUserName, user);
            case BANNED_USER ->
                    "User '%s' banned user '%s'.".formatted(loggedUserName, user);
            case UNBANNED_USER ->
                    "User '%s' unbanned user '%s'.".formatted(loggedUserName, user);
            case DELETED_USER ->
                    "User '%s' deleted user '%s'.".formatted(loggedUserName, user);
            case RESTORED_USER ->
                    "User '%s' restored user '%s'.".formatted(loggedUserName, user);
            default -> "User '%s' did an unknown action!".formatted(loggedUserName);
        });
        createLog(logMessage);
    }

    public void createInvoiceActions(InvoiceType invoiceType, Long invoiceId) {
        Log logMessage = new Log();
        String loggedUserName = saveUser(logMessage);
        logMessage.setInvoiceId(invoiceId);
            switch (invoiceType) {
                case SELL -> {
                    logMessage.setLogType(LogType.CREATED_SELL_INVOICE);
                    logMessage.setAction("User '%s' created sale Invoice#'%s'.".formatted(loggedUserName, invoiceId));
                }
                case REPAIR -> {
                    logMessage.setLogType(LogType.CREATED_REPAIR_INVOICE);
                    logMessage.setAction("User '%s' created repair Invoice#'%s'.".formatted(loggedUserName, invoiceId));
                }
                case BUY -> {
                    logMessage.setLogType(LogType.CREATED_BUY_INVOICE);
                    logMessage.setAction("User '%s' created buy Invoice#'%s'.".formatted(loggedUserName, invoiceId));
                }
                case ACCESSORIES -> {
                    logMessage.setLogType(LogType.CREATED_ACCESSORIES_INVOICE);
                    logMessage.setAction("User '%s' created accessory Invoice#'%s'.".formatted(loggedUserName, invoiceId));
                }
                default -> logMessage.setAction("User '%s' did an unknown action!".formatted(loggedUserName));
            }
            createLog(logMessage);
        }

    public void invalidateInvoiceActions(Long invoiceId){
        Log logMessage = new Log();
        String loggedUserName = saveUser(logMessage);
        logMessage.setInvoiceId(invoiceId);
        logMessage.setLogType(LogType.INVALIDATED_INVOICE);
        logMessage.setAction("User '%s' invalidated Invoice#'%s'.".formatted(loggedUserName, invoiceId));
        createLog(logMessage);
    }
}
