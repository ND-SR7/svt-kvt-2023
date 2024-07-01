package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.CommentDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.ImageDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.*;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
import com.ftn.ac.rs.svtkvt2023.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/posts")
public class PostController {

    PostService postService;
    PostSearchService postSearchService;
    GroupService groupService;
    UserService userService;
    ImageService imageService;
    CommentService commentService;
    ReactionService reactionService;
    FileService fileService;
    TokenUtils tokenUtils;

    private static final Logger logger = LogManager.getLogger(PostController.class);

    @Autowired
    public PostController(PostService postService, UserService userService, GroupService groupService, PostSearchService postSearchService,
                          ImageService imageService, CommentService commentService, ReactionService reactionService, FileService fileService,
                          TokenUtils tokenUtils) {
        this.postService = postService;
        this.postSearchService = postSearchService;
        this.userService = userService;
        this.groupService = groupService;
        this.imageService = imageService;
        this.commentService = commentService;
        this.reactionService = reactionService;
        this.fileService = fileService;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getOne(@PathVariable String id) {
        logger.info("Finding post for id: {}", id);
        Post post = postService.findById(Long.parseLong(id));

        if (post == null) {
            logger.error("Post not found for id: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Creating response with post");
        PostDTO postDTO = new PostDTO(post);
        logger.info("Created and sent response with post");

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ImageDTO>> getImagesForPost(@PathVariable String id) {
        logger.info("Finding images for post with id: {}", id);
        List<Image> images = imageService.findImagesForPost(Long.parseLong(id));
        List<ImageDTO> imageDTOS = new ArrayList<>();

        logger.info("Creating response with post's images");
        for (Image image: images) {
            imageDTOS.add(new ImageDTO(image));
        }
        logger.info("Created and sent response with post's images");

        return new ResponseEntity<>(imageDTOS, HttpStatus.OK);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsForPost(@PathVariable String id) {
        logger.info("Finding comments for post with id: {}", id);
        List<Comment> comments = commentService.findCommentsForPost(Long.parseLong(id));
        List<CommentDTO> commentDTOS = new ArrayList<>();

        logger.info("Creating response with post's comments");
        for (Comment comment: comments) {
            commentDTOS.add(new CommentDTO(comment));
        }
        logger.info("Created and sent response with post's comments");

        return new ResponseEntity<>(commentDTOS, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<PostDTO>> getAll() {
        logger.info("Finding all posts");
        List<Post> posts = postService.findAll();
        List<PostDTO> postsDTOS = new ArrayList<>();

        logger.info("Creating response with posts");
        for (Post post: posts) {
            postsDTOS.add(new PostDTO(post));
        }
        logger.info("Created and sent response with posts");

        return new ResponseEntity<>(postsDTOS, HttpStatus.OK);
    }

    @GetMapping("/homepage")
    public ResponseEntity<List<PostDTO>> getHomepagePosts(@RequestHeader("authorization") String token) {
        User user = getUserFromToken(token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Finding all homepage posts");
        List<Post> posts = postService.findHomepagePosts(user.getId());
        List<PostDTO> postsDTOS = new ArrayList<>();

        logger.info("Creating response with homepage posts");
        for (Post post: posts) {
            PostDTO postDTO = new PostDTO(post);
            Group group = groupService.checkIfPostInGroup(post.getId());
            if (group != null)
                postDTO.setBelongsToGroupId(group.getId());
            postsDTOS.add(postDTO);
        }
        logger.info("Created and sent response with homepage posts");

        return new ResponseEntity<>(postsDTOS, HttpStatus.OK);
    }

    @GetMapping("/homepage/sort/{order}")
    public ResponseEntity<List<PostDTO>> getHomepagePostsSorted(@PathVariable String order,
                                                          @RequestHeader("authorization") String token) {
        User user = getUserFromToken(token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Sorting homepage posts");
        List<Post> posts = new ArrayList<>();
        if (order.equals("asc"))
            posts = postService.findHomepagePostsSortedAsc(user.getId());
        else if (order.equals("desc"))
            posts = postService.findHomepagePostsSortedDesc(user.getId());

        List<PostDTO> postsDTOS = new ArrayList<>();

        logger.info("Creating response with sorted homepage posts");
        for (Post post: posts) {
            PostDTO postDTO = new PostDTO(post);
            Group group = groupService.checkIfPostInGroup(post.getId());
            if (group != null)
                postDTO.setBelongsToGroupId(group.getId());
            postsDTOS.add(postDTO);
        }
        logger.info("Created and sent response with sorted homepage posts");

        return new ResponseEntity<>(postsDTOS, HttpStatus.OK);
    }

    @GetMapping("/group/{id}")
    public ResponseEntity<List<PostDTO>> getAllForGroup(@PathVariable String id) {
        logger.info("Finding all posts for group with id: {}", id);
        List<Long> postsIds = groupService.findPostsByGroupId(Long.parseLong(id));

        if (postsIds == null) {
            logger.error("Posts not found for group with id: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<PostDTO> postDTOS = new ArrayList<>();

        logger.info("Creating response with posts for group {}", id);
        for (Long postId: postsIds) {
            Post post = postService.findById(postId);
            PostDTO postDTO = new PostDTO(post);
            postDTO.setBelongsToGroupId(Long.parseLong(id));
            postDTOS.add(postDTO);
        }
        logger.info("Created and sent response with posts for group {}", id);

        return new ResponseEntity<>(postDTOS, HttpStatus.OK);
    }

    @GetMapping("/group/{id}/user")
    public ResponseEntity<Boolean> checkUserInGroup(@PathVariable String id,
                                                        @RequestHeader("authorization") String token) {
        User user = getUserFromToken(token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (userService.checkUserIsAdmin(user.getId())) {
            logger.info("User is system admin and has access to group");
            return new ResponseEntity<>(true, HttpStatus.OK);
        }

        logger.info("Checking if user is in group with id: {}", id);
        Boolean userInGroup = groupService.checkUser(Long.parseLong(id), user.getId());

        if (!userInGroup) {
            logger.error("User not found in group with id: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("User found in group with id: {}", id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<PostDTO> addPost(@RequestParam("file") MultipartFile file,
                                           @RequestPart("post") @Validated PostDTO newPost) {
        logger.info("Creating post from DTO");
        Post createdPost = postService.createPost(newPost, file);

        if (createdPost == null) {
            logger.error("Post couldn't be created from DTO");
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        logger.info("Checking if post has images and adding them");
        if (newPost.getImages() != null) {
            for (ImageDTO imageDTO: newPost.getImages()) { //kada se pravi post, ne postoji id post-a
                imageDTO.setBelongsToPostId(createdPost.getId()); //pa se mora ovde postaviti ili image nece pripadati nikom
                imageService.createImage(imageDTO);
            }
        }

        logger.info("Creating response with new post");
        PostDTO postDTO = new PostDTO(createdPost);
        logger.info("Created and sent response with new post");

        return new ResponseEntity<>(postDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<PostDTO> editPost(@PathVariable String id, @RequestBody @Validated PostDTO editedPost) {
        logger.info("Finding original post for id: {}", id);
        Post oldPost = postService.findById(Long.parseLong(id));

        if (oldPost == null) {
            logger.error("Original post not found for id: {}", id);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        logger.info("Applying changes");
        oldPost.setContent(editedPost.getContent());

        if (!editedPost.getImages().isEmpty()) {
            imageService.deletePostImages(Long.parseLong(id));
            for (ImageDTO imageDTO: editedPost.getImages())
                imageService.createImage(imageDTO);
        }

        oldPost = postService.updatePost(oldPost);

        logger.info("Creating response with updated post");
        PostDTO updatedPost = new PostDTO(oldPost);
        logger.info("Created and sent response with updated post");

        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Integer> deletePost(@PathVariable String id) {
        logger.info("Deleting post with id: {}", id);

        postService.deletePostFromGroup(Long.parseLong(id));
        Integer deletedFromAll = postService.deletePost(Long.parseLong(id));
        imageService.deletePostImages(Long.parseLong(id));
        commentService.deletePostComments(Long.parseLong(id));
        reactionService.deletePostReactions(Long.parseLong(id));

        if (deletedFromAll != 0) {
            logger.info("Successfully deleted post with id: {}", id);
            return new ResponseEntity<>(deletedFromAll, HttpStatus.NO_CONTENT);
        }
        logger.error("Failed to delete post with id: {}", id);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/file/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        logger.info("Serving post file with filename: {}", filename);

        var minioResponse = fileService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, minioResponse.headers().get("Content-Disposition"))
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Path.of(filename)))
                .body(new InputStreamResource(minioResponse));
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchPosts(@RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String fullContent,
                                                  @RequestParam(required = false) String fileContent,
                                                  @RequestParam(required = false) Long minLikes,
                                                  @RequestParam(required = false) Long maxLikes,
                                                  @RequestParam(required = false) Long minComments,
                                                  @RequestParam(required = false) Long maxComments,
                                                  @RequestParam(required = false) String commentContent,
                                                  @RequestParam(required = false) String phrase,
                                                  @RequestParam(required = false, defaultValue = "OR") String operator) {
        return postSearchService.searchPosts(title, fullContent, fileContent, minLikes, maxLikes, minComments, maxComments,
                commentContent, phrase, operator);
    }

    private User getUserFromToken(String token) {
        return userService.findByUsername(
                tokenUtils.getUsernameFromToken(
                        token.substring(7)));
    }
}
