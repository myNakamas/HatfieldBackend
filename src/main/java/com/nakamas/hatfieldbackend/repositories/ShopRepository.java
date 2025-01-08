package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query("from Shop where shopName=?1")
    Shop findByName(String hatfield);

    @Query("select s.image.path from Shop s where s.id = ?1")
    Optional<String> findShopImagePath(Long id);
}
