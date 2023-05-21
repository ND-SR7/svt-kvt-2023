package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.GroupDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Group;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/posts")
public class PostController {

    PostService postService;

    GroupService groupService;

    UserService userService;

    AuthenticationManager authenticationManager;

    TokenUtils tokenUtils;

    @Autowired
    public PostController(PostService postService, UserService userService, GroupService groupService,
                          AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.postService = postService;
        this.userService = userService;
        this.groupService = groupService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping()
    public ResponseEntity<List<PostDTO>> getAll(@RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Post> posts = postService.findAll();
        List<PostDTO> postsDTOS = new ArrayList<>();

        for (Post post: posts) {
            postsDTOS.add(new PostDTO(post));
        }

        return new ResponseEntity<>(postsDTOS, HttpStatus.OK);
    }

    @GetMapping("/group/{id}")
    public ResponseEntity<List<PostDTO>> getAllForGroup(@PathVariable String id,
                                                        @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Long> postsIds = groupService.findPostsByGroupId(Long.parseLong(id));

        if (postsIds == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<PostDTO> postDTOS = new ArrayList<>();

        for (Long postId: postsIds) {
            Post post = postService.findById(postId);
            postDTOS.add(new PostDTO(post));
        }

        return new ResponseEntity<>(postDTOS, HttpStatus.OK);
    }
}
