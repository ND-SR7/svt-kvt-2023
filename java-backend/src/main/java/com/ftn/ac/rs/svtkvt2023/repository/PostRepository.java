package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<List<Post>> findAllByCreationDate(LocalDateTime creationDate);

    Optional<List<Post>> findAllByPostedBy(User user);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into group_posts (group_id, post_id) values (:groupId, :postId)")
    Integer saveGroupPost(@Param("groupId") Long groupId, @Param("postId") Long postId);

    @Transactional
    Integer deletePostById(Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "delete from group_posts where post_id = :id")
    Integer deletePostFromGroup(@Param("id") Long id);
}
