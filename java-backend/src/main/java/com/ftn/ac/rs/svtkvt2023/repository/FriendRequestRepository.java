package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query(nativeQuery = true,
            value = "select * from friend_requests where from_user_id = :userId and deleted = false")
    Optional<List<FriendRequest>> findAllByFromUser(@Param("userId") Long userId);

    @Query(nativeQuery = true,
            value = "select * from friend_requests where to_user_id = :userId and deleted = false")
    Optional<List<FriendRequest>> findAllByToUser(@Param("userId") Long userId);

    @Transactional
    Integer deleteFriendRequestById(Long id);
}
