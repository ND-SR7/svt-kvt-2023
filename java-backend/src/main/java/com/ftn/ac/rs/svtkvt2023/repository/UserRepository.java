package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByUsername(String username);

    @Query(nativeQuery = true,
            value = "select * from users where id in (select user_id from user_friends where friend_id = :userId)" +
                    "or id in (select friend_id from user_friends where user_id = :userId)")
    Optional<List<User>> findFriendsByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true,
            value = "select * from users " +
                    "where (first_name like concat('%', :name1, '%') or last_name like concat('%', :name1, '%') or " +
                    "first_name like concat('%', :name2, '%') or last_name like concat('%', :name2, '%')) " +
                    "and deleted = false;")
    Optional<List<User>> findUsersByQuery(@Param("name1") String name1, @Param("name2") String name2);

    @Transactional
    @Modifying
    Integer deleteUserById(Long id);
}
