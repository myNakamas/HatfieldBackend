package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Shop extends AbstractPersistable<Long> {

    private String shopName;
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "shop")
    private List<InventoryItem> inventory;
    private String address;
    private String phone;
    private String email;
    private String vatNumber;
    private String regNumber;

    @OneToOne(cascade = CascadeType.ALL)
    private ShopSettings settings;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true,mappedBy = "shop")
    private List<User> users;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "shop")
    private List<Ticket> tickets;

//    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
//    @JoinColumn(name="shop_id")
//    private List<Sales> sales;
}
