package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ItemPropertyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.shop.ItemPropertyView(m.id,m.model) from Model m")
    List<ItemPropertyView> findAllModels();
}
