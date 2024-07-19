package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.TicketFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    @Query("from Ticket t where t.shop.id = ?1")
    List<Ticket> findAllForShop(Long id);
    @Query("from Ticket t where t.client.id = ?1")
    List<Ticket> findAllForClient(UUID id);
    @Query("""
        select new com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView(t, client, creator) 
    from Ticket t 
    join User client on t.client = client 
    join User creator on t.createdBy = creator 
    where t.id = ?1""")
    TicketView getTicketView(Long id);
}
