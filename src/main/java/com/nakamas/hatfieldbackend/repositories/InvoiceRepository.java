package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    @Query("from Invoice i where i.client.id = ?1")
    List<Invoice> findAllByClient_Id(UUID clientId);
    @Query("from Invoice i where i.ticketId = ?1 order by i.timestamp desc")
    List<Invoice> findByTicketId(Long id);
}
