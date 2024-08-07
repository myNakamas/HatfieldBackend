package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserAndPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    @Query("""
             select u
             from User u
             where u.username like ?1
             or u.email like ?1
            """)
    Optional<User> findUser(String username);

    @Query("""
             select u
             from User u
             join u.phones p
             where p.phone like ?1
            """)
    Optional<User> findUserByPhone(String phone);

    @Query("""
             select u
             from User u
             join u.phones p
             where p.phone like concat("%",?1,"%")
            """)
    List<User> findAllUsersByPhone(String phone);

    @Query("""
             select u
             from User u
             where u.email like concat("%",?1,"%")
            """)
    List<User> findAllUsersByEmail(String email);

    @Query("""
             select u
             from User u
             where u.fullName like concat("%",?1,"%")
            """)
    List<User> findAllUsersByFullName(String fullName);

    @Query("""
             select u
             from User u
             where u.username = ?1 or (u.email = ?2 and u.email not like '')
            """)
    List<User> uniqueUserExists(String username, String email);

    @Modifying
    @Query("UPDATE User u SET u.isBanned = ?2 where u.id = ?1")
    void setBanned(UUID userId, boolean isBanned);

    @Query("""
            select new com.nakamas.hatfieldbackend.models.views.outgoing.user.UserAndPhone(u,p)
            from User u
            join u.phones p
            where p.phone in ?1
            """)
    List<UserAndPhone> findUniquePhones(List<String> phones);
}
