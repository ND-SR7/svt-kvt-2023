package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    Post findById(Long id);

    List<Post> findByCreationDate(LocalDateTime creationDate);

    List<Post> findByPostedBy(User user);

    List<Post> findAll();

    Post createPost(PostDTO postDTO);

    Post updatePost(Post post);

    Long deletePost(Long id);
}
