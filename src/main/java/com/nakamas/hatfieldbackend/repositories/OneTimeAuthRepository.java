package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.OneTimeAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OneTimeAuthRepository extends JpaRepository<OneTimeAuthToken, Long> {
    @Query("from OneTimeAuthToken o where o.isUsed = false and o.expirationDate > local datetime and o.token = ?1")
    Optional<OneTimeAuthToken> findValidToken(UUID oneTimeToken);
}
