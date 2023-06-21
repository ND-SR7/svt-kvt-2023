package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(String name);

    Optional<List<Group>> findAllByCreationDate(LocalDateTime creationDate);

    @Transactional
    Integer deleteGroupById(Long id);

    @Query(nativeQuery = true,
            value = "select id from posts p where p.id in (select post_id from group_posts where group_id = :groupId)")
    Optional<List<Long>> findPostsByGroupId(@Param("groupId") Long groupId);

    @Query(nativeQuery = true,
            value = "select count(*) from group_members where group_id = :groupId and member_id = :userId")
    Integer findUserInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
