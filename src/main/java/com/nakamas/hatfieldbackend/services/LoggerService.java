package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.LogFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.LogView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoggerService {
    private final LogRepository logRepository;
    private final UserRepository userRepository;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String LOG_SEPARATOR = ";";


    private String saveUser(Log logMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return "Not an authenticated user! ATTENTION!!!";
        User loggedUser = (User) authentication.getPrincipal();
        logMessage.setUserId(loggedUser.getId());
        logMessage.setShopId(loggedUser.getShop().getId());
        return loggedUser.getFullName();
    }

    public PageView<LogView> getLogs(LogFilter filter, PageRequest pageRequest) {
        Page<Log> all = logRepository.findAll(filter, pageRequest);
        return new PageView<>(all.map((log) -> new LogView(log, getUserProfile(log.getUserId()))));
    }

    private UserProfile getUserProfile(UUID id) {
        if (id == null) return null;
        return userRepository.findById(id).map(UserProfile::new).orElse(null);
    }

    public void createLog(Log log, String info) {
        log.setAction(log.getLogType().getMessage().formatted(saveUser(log), info));
        logRepository.save(log);
    }

    public void createLog(Log log, Long id) {
        log.setAction(log.getLogType().getMessage().formatted(saveUser(log), id));
        logRepository.save(log);
    }

    public void createLog(Log log, InventoryItem item, Long ticketId, Integer count) {
        log.setTicketId(ticketId);
        log.setItemId(item.getId());
        log.setAction(log.getLogType().getMessage().formatted(saveUser(log), count, item.getName(), ticketId));
        logRepository.save(log);
    }

    public void createLog(Log log, InventoryItem item, Integer oldCount, Integer newCount) {
        log.setItemId(item.getId());
        log.setAction(log.getLogType().getMessage().formatted(saveUser(log), item.getName(), oldCount, newCount));
        logRepository.save(log);
    }

    public void createLog(Log log, InventoryItem item, Integer count) {
        log.setItemId(item.getId());
        log.setAction(log.getLogType().getMessage().formatted(saveUser(log), count, item.getName()));
        logRepository.save(log);
    }

    //for updating logs
    public void createLog(Log log, String info, String updateInfo) {
        log.setAction(log.getLogType().getMessage().formatted(saveUser(log), info, updateInfo));
        logRepository.save(log);
    }

    //region updating functions
    public String categoryUpdateCheck(Category category, CategoryView view) {
        StringBuilder updateInfo = new StringBuilder(" ");

        if (!Objects.equals(category.getName(), view.name())) {
            updateInfo.append("Name updated from ").append(category.getName()).append(" to ").append(view.name()).append(LOG_SEPARATOR);
        }
        if (category.getType() != view.itemType())
            updateInfo.append("Type updated from ").append(category.getType()).append(" to ").append(view.itemType()).append(LOG_SEPARATOR);
        if (category.getFields() != view.columns())
            updateInfo.append("Fields updated from ").append(String.join(", ", category.getFields())).append(" to ").append(String.join(", ", view.columns())).append(LOG_SEPARATOR);

        return updateInfo.toString();
    }

    public String shopUpdateCheck(Shop shop, CreateShop view) {
        StringBuilder updateInfo = new StringBuilder(" ");
        //shop part
        if (!Objects.equals(shop.getShopName(), view.shopName()))
            updateInfo.append("Name updated from ").append(shop.getShopName()).append(" to ").append(view.shopName()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getAddress(), view.address()))
            updateInfo.append("Address updated from ").append(shop.getAddress()).append(" to ").append(view.address()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getPhone(), view.phone()))
            updateInfo.append("Phone updated from ").append(shop.getPhone()).append(" to ").append(view.phone()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getEmail(), view.email()))
            updateInfo.append("Email updated from ").append(shop.getEmail()).append(" to ").append(view.email()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getVatNumber(), view.vatNumber()))
            updateInfo.append("Vat number updated from ").append(shop.getVatNumber()).append(" to ").append(view.vatNumber()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getRegNumber(), view.regNumber()))
            updateInfo.append("Reg number updated from ").append(shop.getRegNumber()).append(" to ").append(view.regNumber()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getTemplates().getAboutPage(), view.templates().getAboutPage()))
            updateInfo.append("About page updated from ").append(shop.getTemplates().getAboutPage()).append(" to ").append(view.templates().getAboutPage()).append(LOG_SEPARATOR);
        //shop settings part
        if (!Objects.equals(shop.getSettings().getPrimaryColor(), view.shopSettingsView().primaryColor()))
            updateInfo.append("Primary color updated from ").append(shop.getSettings().getPrimaryColor()).append(" to ").append(view.shopSettingsView().primaryColor()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getSettings().getSecondaryColor(), view.shopSettingsView().secondaryColor()))
            updateInfo.append("Secondary color updated from ").append(shop.getSettings().getSecondaryColor()).append(" to ").append(view.shopSettingsView().secondaryColor()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getSettings().getGmail(), view.shopSettingsView().gmail()))
            updateInfo.append("Gmail updated from ").append(shop.getSettings().getGmail()).append(" to ").append(view.shopSettingsView().gmail()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getSettings().getGmailPassword(), view.shopSettingsView().gmailPassword()))
            updateInfo.append("Gmail password updated from ").append(shop.getSettings().getGmailPassword()).append(" to ").append(view.shopSettingsView().gmailPassword()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getSettings().getPrinterIp(), view.shopSettingsView().printerIp()))
            updateInfo.append("Printer IP updated from ").append(shop.getSettings().getPrinterIp()).append(" to ").append(view.shopSettingsView().printerIp()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getSettings().getPrinterModel(), view.shopSettingsView().printerModel()))
            updateInfo.append("Printer model updated from ").append(shop.getSettings().getPrinterModel()).append(" to ").append(view.shopSettingsView().printerModel()).append(LOG_SEPARATOR);
        if (!Objects.equals(shop.getSettings().getSmsApiKey(), view.shopSettingsView().smsApiKey()))
            updateInfo.append("SMS API key updated from ").append(shop.getSettings().getSmsApiKey()).append(" to ").append(view.shopSettingsView().smsApiKey()).append(LOG_SEPARATOR);
        if (shop.getSettings().isEmailEnabled() != view.shopSettingsView().emailNotificationsEnabled())
            updateInfo.append("Email permissions updated from ").append(shop.getSettings().isEmailEnabled()).append(" to ").append(view.shopSettingsView().emailNotificationsEnabled()).append(LOG_SEPARATOR);
        if (shop.getSettings().isPrintEnabled() != view.shopSettingsView().printEnabled())
            updateInfo.append("Print permissions updated from ").append(shop.getSettings().isPrintEnabled()).append(" to ").append(view.shopSettingsView().printEnabled()).append(LOG_SEPARATOR);
        if (shop.getSettings().isSmsEnabled() != view.shopSettingsView().smsNotificationsEnabled())
            updateInfo.append("SMS permissions updated from ").append(shop.getSettings().isSmsEnabled()).append(" to ").append(view.shopSettingsView().smsNotificationsEnabled()).append(LOG_SEPARATOR);

        return updateInfo.toString();
    }

    public String ticketUpdateCheck(Ticket ticket, CreateTicket view) {
        StringBuilder updateInfo = new StringBuilder(" ");

        if (view.deviceModel() != null && !Objects.equals(ticket.getDeviceModelString(), view.deviceModel()))
            updateInfo.append("Model updated from ").append(ticket.getDeviceModelString()).append(" to ").append(view.deviceModel()).append(LOG_SEPARATOR);
        if (view.deviceBrand() != null && !Objects.equals(ticket.getDeviceBrandString(), view.deviceBrand()))
            updateInfo.append("Brand updated from ").append(ticket.getDeviceBrandString()).append(" to ").append(view.deviceBrand()).append(LOG_SEPARATOR);
        if (view.deviceLocation() != null && !Objects.equals(ticket.getDeviceLocationString(), view.deviceLocation()))
            updateInfo.append("Location updated from ").append(ticket.getDeviceLocationString()).append(" to ").append(view.deviceLocation()).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getCustomerRequest(), view.customerRequest()))
            updateInfo.append("Customer request updated from ").append(ticket.getCustomerRequest()).append(" to ").append(view.customerRequest()).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getDeviceProblemExplanation(), view.problemExplanation()))
            updateInfo.append("Device problem updated from ").append(ticket.getDeviceProblemExplanation()).append(" to ").append(view.problemExplanation()).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getDeviceCondition(), view.deviceCondition()))
            updateInfo.append("Device condition updated from ").append(ticket.getDeviceCondition()).append(" to ").append(view.deviceCondition()).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getDevicePassword(), view.devicePassword()))
            updateInfo.append("Device password updated from ").append(ticket.getDevicePassword()).append(" to ").append(view.devicePassword()).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getSerialNumberOrImei(), view.serialNumberOrImei()))
            updateInfo.append("IMEI updated from ").append(ticket.getSerialNumberOrImei()).append(" to ").append(view.serialNumberOrImei()).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getAccessories(), view.accessories()))
            updateInfo.append("Accessories updated from ").append(ticket.getAccessories()).append(" to ").append(view.accessories()).append(LOG_SEPARATOR);
        if (isZonedDateTimeDifferent(ticket.getDeadline(), view.deadline()))
            updateInfo.append("Deadline updated from ").append(ticket.getDeadline().format(dtf)).append(" to ").append(view.deadline().withZoneSameInstant(ticket.getDeadline().getZone()).format(dtf)).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getNotes(), view.notes()))
            updateInfo.append("Notes updated from ").append(ticket.getNotes()).append(" to ").append(view.notes()).append(LOG_SEPARATOR);
        if (!Objects.equals(ticket.getStatus(), view.status()))
            updateInfo.append("Status updated from ").append(ticket.getStatus().toString()).append(" to ").append(view.status()).append(LOG_SEPARATOR);
        if (isBigDecimalDifferent(ticket.getTotalPrice(), view.totalPrice()))
            updateInfo.append("Total price updated from ").append(ticket.getTotalPrice().toString()).append(" to ").append(view.totalPrice()).append(LOG_SEPARATOR);
        if (isBigDecimalDifferent(ticket.getDeposit(), view.deposit()))
            updateInfo.append("Deposit updated from ").append(ticket.getDeposit().toString()).append(" to ").append(view.deposit()).append(LOG_SEPARATOR);
        if (ticket.getClient() != null && ticket.getClient().getId() != null && !Objects.equals(ticket.getClient().getId(), view.clientId()))
            updateInfo.append("Client updated from ").append(ticket.getClient().getId().toString()).append(" to ").append(view.clientId()).append(LOG_SEPARATOR);
        return updateInfo.toString();
    }

    private boolean isZonedDateTimeDifferent(ZonedDateTime first, ZonedDateTime second) {
        if (first == null && second == null) return false;
        if (first != null && second != null)
            return first.compareTo(second.withZoneSameInstant(first.getZone())) != 0;
        return true;

    }

    private boolean isBigDecimalDifferent(BigDecimal first, BigDecimal second) {
        if (first == null && second == null) return false;
        if (first != null && second != null) return first.compareTo(second) != 0;
        return true;
    }

    public String userUpdateCheck(User user, CreateUser view) {
        StringBuilder updateInfo = new StringBuilder(" ");

        if (!Objects.equals(user.getUsername(), view.username()))
            updateInfo.append("Username updated from ").append(user.getUsername()).append(" to ").append(view.username()).append(LOG_SEPARATOR);
        if (!Objects.equals(user.getFullName(), view.fullName()))
            updateInfo.append("Full name updated from ").append(user.getFullName()).append(" to ").append(view.fullName()).append(LOG_SEPARATOR);
        if (!Objects.equals(user.getPassword(), view.password())) updateInfo.append("Password updated. ");
        if (!Objects.equals(user.getRole().getRole(), view.role().getRole()))
            updateInfo.append("Role updated from ").append(user.getRole().getRole()).append(" to ").append(view.role().getRole()).append(LOG_SEPARATOR);
        if (!Objects.equals(user.getEmail(), view.email()))
            updateInfo.append("Email updated from ").append(user.getEmail()).append(" to ").append(view.email()).append(LOG_SEPARATOR);
        if (!Objects.equals(user.getShop().getId(), view.shopId()))
            updateInfo.append("Shop id updated from ").append(user.getShop().getId()).append(" to ").append(view.shopId()).append(LOG_SEPARATOR);
        if (user.getIsActive() != view.isActive())
            updateInfo.append("Activity updated from ").append(user.getIsActive()).append(" to ").append(view.isActive()).append(LOG_SEPARATOR);
        if (user.getIsBanned() != view.isBanned())
            updateInfo.append("Ban status updated from ").append(user.getIsBanned()).append(" to ").append(view.isBanned()).append(LOG_SEPARATOR);
        if (user.getSmsPermission() != view.smsPermission())
            updateInfo.append("SMS permissions updated from ").append(user.getSmsPermission()).append(" to ").append(view.smsPermission()).append(LOG_SEPARATOR);
        if (user.getEmailPermission() != view.emailPermission())
            updateInfo.append("Email permissions updated from ").append(user.getEmailPermission()).append(" to ").append(view.emailPermission()).append(LOG_SEPARATOR);

        return updateInfo.toString();
    }

    public String itemUpdateCheck(InventoryItem item, CreateInventoryItem view) {
        StringBuilder updateInfo = new StringBuilder(" ");

        if (!Objects.equals(item.getShop().getId(), view.shopId()))
            updateInfo.append("Shop updated from ").append(item.getShop().getId()).append(" to ").append(view.shopId()).append(LOG_SEPARATOR);
        if (!Objects.equals(item.getCategoryId(), view.categoryId()))
            updateInfo.append("Category updated from ").append(item.getCategoryId()).append(" to ").append(view.categoryId()).append(LOG_SEPARATOR);
        if (!Objects.equals(item.getModelString(), view.model()))
            updateInfo.append("Model updated from ").append(item.getModelString()).append(" to ").append(view.model()).append(LOG_SEPARATOR);
        if (!Objects.equals(item.getBrandString(), view.brand()))
            updateInfo.append("Brand updated from ").append(item.getBrandString()).append(" to ").append(view.brand()).append(LOG_SEPARATOR);
        if (!Objects.equals(item.getName(), view.name()))
            updateInfo.append("Name updated from ").append(item.getName()).append(" to ").append(view.name()).append(LOG_SEPARATOR);
        if (!Objects.equals(item.getPurchasePrice().toString(), view.purchasePrice().toString()))
            updateInfo.append("Purchase price updated from ").append(item.getPurchasePrice().toString()).append(" to ").append(view.purchasePrice()).append(LOG_SEPARATOR);
        if (!Objects.equals(item.getSellPrice().toString(), view.sellPrice().toString()))
            updateInfo.append("Sell price updated from ").append(item.getSellPrice().toString()).append(" to ").append(view.sellPrice()).append(LOG_SEPARATOR);
        if (!Objects.equals(item.getCount(), view.count()))
            updateInfo.append("Count updated from ").append(item.getCount()).append(" to ").append(view.count()).append(LOG_SEPARATOR);

        return updateInfo.toString();
    }
    //endregion
}
