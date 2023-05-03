package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
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
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
            default -> "User '%s' did an unknown action!";
        });
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
//            case USED_PART -> moje bi za specialna funkciq iska i ticket i item?
//                    "User '%s' deleted Category.".formatted(loggedUserName);
            case UPDATE_ITEM_COUNT ->
                    "User '%s' updated item '%s' count from '%s' to '%s'.".formatted(loggedUserName, item.getName(), item.getCount(), count);
            case UPDATE_ITEM -> "User '%s' updated item '%s'.".formatted(loggedUserName, item.getName());
            case SCRAPPED_PART ->// not implemented yet
                    "User '%s' scrapped '%s' '%s'.".formatted(loggedUserName, count, item.getName());
            case REMOVE_ITEM_FROM_SHOPPING_LIST ->//not implemented yet
                    "User '%s' removed item '%s' from the shopping list.".formatted(loggedUserName, item.getName());
            default -> "User '%s' did an unknown action!";
        });
        createLog(logMessage);
    }

    //old functions
    public void createLog(String message, UUID user, Long ticketId) {
        createLog(Log.builder().action(message).userId(user).ticketId(ticketId).build());
    }

    public void createLogUsedItem(UsedPart usedPart, Long ticketId, User user) {
        Log build = Log.builder().action("User '%s' has used part '%s' for Ticket#%s ".formatted(user.getFullName(), usedPart.getItem(), ticketId)).userId(user.getId()).ticketId(ticketId).itemId(usedPart.getId()).build();
        createLog(build);
    }

    public void createLogUpdatedRequiredItemAmount(InventoryItem item, User user) {
        Log build = Log.builder().userId(user.getId()).action("User %s updated the needed required amount of item %s".formatted(user.getFullName(), item.getName())).build();
        createLog(build);
    }

}
