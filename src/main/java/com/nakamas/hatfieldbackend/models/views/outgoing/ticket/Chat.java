package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import java.util.List;

public record Chat(List<ChatMessageView> sent, List<ChatMessageView> received) {
}
