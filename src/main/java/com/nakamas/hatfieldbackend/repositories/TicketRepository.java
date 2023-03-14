package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView(t) " +
            "from Ticket t " +
            "where t.shop.id = ?1 " +
            "order by t.priority desc ")
    Page<TicketView> findAllByShopId(Long shopId, PageRequest pageRequest);
    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView(t) " +
            "from Ticket t " +
            "where t.shop.id = ?1 " +
            "and t.status > 5 " +
            "order by t.priority desc ")
    Page<TicketView> findAllByShopIdAndFinished(Long shopId, PageRequest pageRequest);
    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView(t) " +
            "from Ticket t " +
            "where t.shop.id = ?1 " +
            "and t.status < 6 and t.status > 0 " +
            "order by t.priority desc ")
    Page<TicketView> findAllByShopIdAndOpen(Long shopId, PageRequest pageRequest);
}
