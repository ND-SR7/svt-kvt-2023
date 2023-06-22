package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.ImageDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.PostDTO;
import com.ftn.ac.rs.svtkvt2023.model.dto.ReactionDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Image;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.Reaction;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/reactions")
public class ReactionController {

    ReactionService reactionService;

    PostService postService;

    CommentService commentService;

    UserService userService;

    AuthenticationManager authenticationManager;

    TokenUtils tokenUtils;

    @Autowired
    public ReactionController(ReactionService reactionService, PostService postService, CommentService commentService,
                              UserService userService, AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.reactionService = reactionService;
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionDTO> getOne(@PathVariable String id,
                                              @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Reaction reaction = reactionService.findById(Long.parseLong(id));

        if (reaction == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ReactionDTO reactionDTO = new ReactionDTO(reaction);

        return new ResponseEntity<>(reactionDTO, HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<List<ReactionDTO>> getReactionsForPost(@PathVariable String id,
                                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Reaction> reactions = reactionService.findReactionsForPost(Long.parseLong(id));
        List<ReactionDTO> reactionDTOS = new ArrayList<>();

        for (Reaction reaction: reactions) {
            reactionDTOS.add(new ReactionDTO(reaction));
        }

        return new ResponseEntity<>(reactionDTOS, HttpStatus.OK);
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<List<ReactionDTO>> getReactionsForComment(@PathVariable String id,
                                                                 @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Reaction> reactions = reactionService.findReactionsForComment(Long.parseLong(id));
        List<ReactionDTO> reactionDTOS = new ArrayList<>();

        for (Reaction reaction: reactions) {
            reactionDTOS.add(new ReactionDTO(reaction));
        }

        return new ResponseEntity<>(reactionDTOS, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ReactionDTO> addReactions(@RequestBody @Validated ReactionDTO newReaction,
                                           @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Reaction createdReaction = reactionService.createReaction(newReaction);

        if (createdReaction == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);

        ReactionDTO reactionDTO = new ReactionDTO(createdReaction);

        return new ResponseEntity<>(reactionDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteReaction(@PathVariable String id,
                                      @RequestHeader("authorization") String token) {
        String cleanToken = token.substring(7); //izbacivanje 'Bearer' iz tokena
        String username = tokenUtils.getUsernameFromToken(cleanToken); //izvlacenje username-a iz tokena
        User user = userService.findByUsername(username); //provera da li postoji u bazi

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Integer deleted = reactionService.deleteReaction(Long.parseLong(id));

        if (deleted != 0)
            return new ResponseEntity(deleted, HttpStatus.NO_CONTENT);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
