package com.nakamas.hatfieldbackend.models.entities;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_table")
@Entity
public class User extends AbstractPersistable<UUID> {
    @Column(unique = true)
    private String username;
    private String fullName;
    private String password;
    @Column(unique = true)
    private String email;
    private String phone;
    @Enumerated
    private UserRole role;

    private Boolean smsPermission = true;
    private Boolean emailPermission = true;

    @OneToMany
    @JoinColumn(name="receiver_id")
    private List<ChatMessage> receivedMessages;
    @OneToMany
    @JoinColumn(name="sender_id")
    private List<ChatMessage> sentMessages;

    private void generateUsername(){
        this.username = "generated LOL";
    }
}
