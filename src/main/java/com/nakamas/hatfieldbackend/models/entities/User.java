package com.nakamas.hatfieldbackend.models.entities;

import com.nakamas.hatfieldbackend.config.AttributeEncryptor;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_table")
@Entity
public class User extends AbstractPersistable<UUID> implements UserDetails {

    @Convert(converter = AttributeEncryptor.class)
    private String username;
    @Convert(converter = AttributeEncryptor.class)
    private String fullName;
    private String password;

    private String firstPassword;

    @Convert(converter = AttributeEncryptor.class)
    private String email;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<UserPhone> phones;

    private UserRole role;
    private Boolean isActive;
    private Boolean isBanned;

    private Boolean smsPermission = true;
    private Boolean emailPermission = true;

    @ManyToOne
    private Shop shop;
    @OneToMany(mappedBy = "receiver")
    private List<ChatMessage> receivedMessages;
    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> sentMessages;
    @OneToMany(mappedBy = "createdBy")
    private List<Invoice> createdInvoices;
    @OneToMany(mappedBy = "client")
    private List<Invoice> clientInvoices;
    @OneToMany(mappedBy = "createdBy")
    private List<Ticket> createdTickets;
    @OneToMany(mappedBy = "client")
    private List<Ticket> clientTickets;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    private Photo image;

    public User(CreateUser user, Shop shop) {
        this.username = user.username();
        this.fullName = user.fullName();
        this.password = user.password();
        this.email = user.email();
        if (user.phones() != null)
            this.phones = user.phones().stream().map(UserPhone::new).toList();
        else
            this.phones = new ArrayList<>();
        this.role = user.role();
        this.isActive = true;
        this.isBanned = false;
        this.shop = shop;
    }


    public void update(CreateUser user) {
        if (user.username() != null) this.username = user.username();
        if (user.fullName() != null) this.fullName = user.fullName();
        if (user.email() != null) this.email = user.email();
        if (user.phones() != null) {
            this.phones.clear();
            this.phones.addAll(user.phones().stream().map(UserPhone::new).toList());
        }
        if (user.smsPermission() != null) this.smsPermission = user.smsPermission();
        if (user.emailPermission() != null) this.emailPermission = user.emailPermission();
    }

    public void updateAsAdmin(CreateUser user, Shop shop) {
        if (user.role() != null) this.role = user.role();
        if (user.isActive() != null) this.isActive = user.isActive();
        if (user.isBanned() != null) this.isBanned = user.isBanned();
        this.shop = shop;
        update(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRole()));
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBanned;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEmailEnabled() {
        return this.emailPermission && this.email != null && !this.email.isBlank();
    }

    public boolean isSMSEnabled() {
        return this.smsPermission && !this.phones.isEmpty();
    }

    public List<String> getPhonesString() {
        return phones.stream().map(UserPhone::getPhoneWithCode).toList();
    }

    public boolean isClientUniqueInfoEmpty() {
        return (fullName == null || fullName.isEmpty()) &&
                (email == null || email.isEmpty()) &&
                (phones == null || phones.isEmpty());
    }

    public boolean isAdmin() {
        return this.role.equals(UserRole.ADMIN);
    }
}
