package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsername(String username);

    /**
     * 2 = ENGINEER("WORKER")
     * 3 = SALESMAN("WORKER")
     */
    @Query("""
            select new com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile(u)
             from User u
             where u.role = 2 or u.role = 3
             and concat( u.fullName,u.username,u.email,u.phones) like %?1%
            """)
    List<UserProfile> findAllWorkers(String searchBy);
}
