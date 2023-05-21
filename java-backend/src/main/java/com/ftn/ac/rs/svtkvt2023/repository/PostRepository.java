package com.ftn.ac.rs.svtkvt2023.repository;

import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<List<Post>> findAllByCreationDate(LocalDateTime creationDate);

    Optional<List<Post>> findAllByPostedBy(User user);

    Long deletePostById(Long id);
}
