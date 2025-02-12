package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.BrandView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("select new com.nakamas.hatfieldbackend.models.views.outgoing.inventory.BrandView(b) from Brand b")
    List<BrandView> findAllBrands();

    @Query("from Brand b where LOWER(b.brand) = LOWER(?1)")
    Brand findByName(String brandValue);
}
