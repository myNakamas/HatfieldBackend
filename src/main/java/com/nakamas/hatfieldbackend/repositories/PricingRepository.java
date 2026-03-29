package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.prices.Pricing;
import com.nakamas.hatfieldbackend.models.views.incoming.PricingView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    @Query("""
    from Pricing p
    where p.deviceType=?1 and
    p.brandId=?2 and
    p.modelId=?3 and
    p.issue=?4
    """)
    Optional<Pricing> findByDeviceTypeAndBrandIdAndModelAndIssue(String deviceType, Long brandId, Long modelId, String issue);

    @Query("""
    select new com.nakamas.hatfieldbackend.models.views.incoming.PricingView(p, b.brand, m.model)
    from Pricing p
    join Brand b on b.id = p.brandId
    join Model m on m.id = p.modelId
    """)
    List<PricingView> findAllPricingViews();

    @Query("""
    select new com.nakamas.hatfieldbackend.models.views.incoming.PricingView(p, b.brand, m.model)
    from Pricing p
    join Brand b on b.id = p.brandId
    join Model m on m.id = p.modelId
    where (:deviceType is null or p.deviceType = :deviceType)
      and (:brand is null or b.brand = :brand)
      and (:model is null or m.model = :model)
    """)
    List<PricingView> findAllWithFilters(
            @Param("deviceType") String deviceType,
            @Param("brand") String brand,
            @Param("model") String model);
}