package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView(t) " +
            "from Ticket t " +
            "where t.shop.id = ?1")
    Page<TicketView> findAllByShopId(Long shopId, PageRequest pageRequest);
}
