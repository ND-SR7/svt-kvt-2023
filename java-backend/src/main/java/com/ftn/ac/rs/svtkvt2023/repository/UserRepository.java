package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstById(Long id);

    Optional<User> findFirstByUsername(String username);

    Long deleteUserById(Long id);
}
