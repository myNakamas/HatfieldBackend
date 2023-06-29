package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class Shop extends AbstractPersistable<Long> {

    private String shopName;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shop")
    private List<InventoryItem> inventory = new ArrayList<>();
    private String address;
    private String phone;
    private String email;
    private String vatNumber;
    private String regNumber;

    @Embedded
    private ShopSettings settings;
    @Embedded
    private ShopPageTemplates templates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shop")
    private List<User> users = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shop")
    private List<Ticket> tickets = new ArrayList<>();

//    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
//    @JoinColumn(name="shop_id")
//    private List<Sales> sales;


    public Shop(String shopName, String address, String phone, String email, ShopSettings settings) {
        this.shopName = shopName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.settings = settings;
        this.templates = new ShopPageTemplates();
    }

    public Shop(CreateShop create) {
        this.shopName = create.shopName();
        this.address = create.address();
        this.phone = create.phone();
        this.email = create.email();
        this.vatNumber = create.vatNumber();
        this.regNumber = create.regNumber();
        this.settings = new ShopSettings(create.shopSettingsView());
        this.templates = new ShopPageTemplates(create.templates());
    }

    public void update(CreateShop updateView) {
        if (updateView.shopName() != null) this.shopName = updateView.shopName();
        if (updateView.address() != null) this.address = updateView.address();
        if (updateView.phone() != null) this.phone = updateView.phone();
        if (updateView.email() != null) this.email = updateView.email();
        if (updateView.vatNumber() != null) this.vatNumber = updateView.vatNumber();
        if (updateView.regNumber() != null) this.regNumber = updateView.regNumber();
        if (updateView.shopSettingsView() != null) this.settings.update(updateView.shopSettingsView());
        if (updateView.templates() != null) this.templates.update(updateView.templates());
    }
}
