package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ItemPropertyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ItemPropertyView(m) from Model m")
    List<ItemPropertyView> findAllModels();

    @Query("select m " +
            "from Model m " +
            "where LOWER(m.model) like LOWER(?1)  and m.brandId = ?2")
    Model findByName(String name, Long brandId);
}
