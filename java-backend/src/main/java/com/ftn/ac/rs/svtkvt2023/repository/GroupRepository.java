package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(String name);

    Optional<List<Group>> findAllByCreationDate(LocalDateTime creationDate);

    Long deleteGroupById(Long id);

    @Query(nativeQuery = true,
            value = "select id from posts p where p.id in (select post_id from group_posts where group_id = 1)")
    Optional<List<Long>> findPostsByGroupId(@Param("groupId") Long groupId);
}
