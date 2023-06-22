package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.CommentDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.ImageDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Comment;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.security.TokenUtils;
import com.ftn.ac.rs.svtkvt2023.service.CommentService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.ReactionService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    CommentService commentService;

    PostService postService;

    UserService userService;

    ReactionService reactionService;

    AuthenticationManager authenticationManager;

    TokenUtils tokenUtils;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserService userService,
                             ReactionService reactionService, AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.reactionService = reactionService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getOne(@PathVariable String id,
                                             @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Comment comment = commentService.findById(Long.parseLong(id));

        if (comment == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        CommentDTO commentDTO = new CommentDTO(comment);

        return new ResponseEntity<>(commentDTO, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CommentDTO> addComment(@RequestBody @Validated CommentDTO newComment,
                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Comment createdComment = commentService.createComment(newComment);

        if (createdComment == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);

        CommentDTO commentDTO = new CommentDTO(createdComment);

        return new ResponseEntity<>(commentDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<CommentDTO> editComment(@PathVariable String id,
                                            @RequestBody @Validated CommentDTO editedComment,
                                            @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Comment oldComment = commentService.findById(Long.parseLong(id));

        if (oldComment == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        oldComment.setText(editedComment.getText());
        oldComment.setTimestamp(LocalDate.parse(editedComment.getTimestamp()));

        oldComment = commentService.updateComment(oldComment);

        CommentDTO updatedComment = new CommentDTO(oldComment);

        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteComment(@PathVariable String id,
                                     @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        commentService.deleteCommentReply(Long.parseLong(id));
        Integer deleted = commentService.deleteComment(Long.parseLong(id));
        reactionService.deleteCommentReactions(Long.parseLong(id));

        if (deleted != 0)
            return new ResponseEntity(deleted, HttpStatus.NO_CONTENT);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
