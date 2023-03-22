package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateChatMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table()
@Entity
public class ChatMessage extends AbstractPersistable<Long> {
    @Column(columnDefinition = "text")
    private String text;
    private LocalDateTime timestamp;
    private LocalDateTime readByReceiver;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    @Column(insertable=false, updatable=false)
    private Long ticket_id;
    private Long randomId;

    public ChatMessage(CreateChatMessage create, User sender, User receiver) {
        this.text = create.text();
        this.timestamp = create.timestamp();
        this.readByReceiver = null;
        this.sender = sender;
        this.receiver = receiver;
        this.ticket_id = create.ticketId();
        this.randomId = create.randomId();
    }
}
