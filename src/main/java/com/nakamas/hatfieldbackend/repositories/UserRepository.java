package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findUserByUsername(String username);

    @Query("""
             select u
             from User u
             where (u.username = ?1 or u.email = ?2)
            """)
    List<User> uniqueUserExists(String username, String email);
}
