package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.ImageDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
import com.ftn.ac.rs.svtkvt2023.service.GroupService;
import com.ftn.ac.rs.svtkvt2023.service.ImageService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/posts")
public class PostController {

    PostService postService;

    GroupService groupService;

    UserService userService;

    ImageService imageService;

    AuthenticationManager authenticationManager;

    TokenUtils tokenUtils;

    @Autowired
    public PostController(PostService postService, UserService userService, GroupService groupService, ImageService imageService,
                          AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.postService = postService;
        this.userService = userService;
        this.groupService = groupService;
        this.imageService = imageService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getOne(@PathVariable String id,
                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Post post = postService.findById(Long.parseLong(id));

        if (post == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        PostDTO postDTO = new PostDTO(post);

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ImageDTO>> getImagesForPost(@PathVariable String id,
                                          @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Image> images = imageService.findImagesForPost(Long.parseLong(id));
        List<ImageDTO> imageDTOS = new ArrayList<>();

        for (Image image: images) {
            imageDTOS.add(new ImageDTO(image));
        }

        return new ResponseEntity<>(imageDTOS, HttpStatus.OK);
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

    @GetMapping("/group/{id}/user")
    public ResponseEntity<Boolean> checkUserInGroup(@PathVariable String id,
                                                        @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Boolean userInGroup = groupService.checkUser(Long.parseLong(id), user.getId());

        if (!userInGroup)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<PostDTO> addPost(@RequestBody @Validated PostDTO newPost,
                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Post createdPost = postService.createPost(newPost);

        if (createdPost == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);

        if (newPost.getImages() != null) {
            for (ImageDTO imageDTO: newPost.getImages()) { //kada se pravi post, ne postoji id post-a
                imageDTO.setBelongsToPostId(createdPost.getId()); //pa se mora ovde postaviti ili image nece pripadati nikom
                imageService.createImage(imageDTO);
            }
        }

        PostDTO postDTO = new PostDTO(createdPost);

        return new ResponseEntity<>(postDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<PostDTO> editPost(@PathVariable String id,
                                              @RequestBody @Validated PostDTO editedPost,
                                              @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Post oldPost = postService.findById(Long.parseLong(id));

        if (oldPost == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        oldPost.setContent(editedPost.getContent());

        oldPost = postService.updatePost(oldPost);

        PostDTO updatedPost = new PostDTO(oldPost);

        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deletePost(@PathVariable String id,
                                      @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        postService.deletePostFromGroup(Long.parseLong(id));
        Integer deletedFromAll = postService.deletePost(Long.parseLong(id));
        imageService.deletePostImages(Long.parseLong(id));

        if (deletedFromAll != 0)
            return new ResponseEntity(deletedFromAll, HttpStatus.NO_CONTENT);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
