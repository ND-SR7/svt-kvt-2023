package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.ReactionDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Reaction;
import com.ftn.ac.rs.svtkvt2023.service.ReactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/reactions")
public class ReactionController {

    ReactionService reactionService;

    private static final Logger logger = LogManager.getLogger(ReactionController.class);

    @Autowired
    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionDTO> getOne(@PathVariable String id) {
        logger.info("Finding reaction for id: {}", id);
        Reaction reaction = reactionService.findById(Long.parseLong(id));

        if (reaction == null) {
            logger.error("Reaction not found for id: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Creating response with reaction");
        ReactionDTO reactionDTO = new ReactionDTO(reaction);
        logger.info("Created and sent response with reaction");

        return new ResponseEntity<>(reactionDTO, HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<List<ReactionDTO>> getReactionsForPost(@PathVariable String id) {
        logger.info("Finding reactions for post with id: {}", id);
        List<Reaction> reactions = reactionService.findReactionsForPost(Long.parseLong(id));
        List<ReactionDTO> reactionDTOS = new ArrayList<>();

        logger.info("Creating response with reactions for post {}", id);
        for (Reaction reaction: reactions) {
            reactionDTOS.add(new ReactionDTO(reaction));
        }
        logger.info("Created and sent response with reactions for post {}", id);

        return new ResponseEntity<>(reactionDTOS, HttpStatus.OK);
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<List<ReactionDTO>> getReactionsForComment(@PathVariable String id) {
        logger.info("Finding reactions for comment with id: {}", id);
        List<Reaction> reactions = reactionService.findReactionsForComment(Long.parseLong(id));
        List<ReactionDTO> reactionDTOS = new ArrayList<>();

        logger.info("Creating response with reactions for comment {}", id);
        for (Reaction reaction: reactions) {
            reactionDTOS.add(new ReactionDTO(reaction));
        }
        logger.info("Created and sent response with reactions for comment {}", id);

        return new ResponseEntity<>(reactionDTOS, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ReactionDTO> addReaction(@RequestBody @Validated ReactionDTO newReaction) {
        logger.info("Creating reaction from DTO");
        Reaction createdReaction = reactionService.createReaction(newReaction);

        if (createdReaction == null) {
            logger.error("Reaction couldn't be created from DTO");
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        logger.info("Creating response");
        ReactionDTO reactionDTO = new ReactionDTO(createdReaction);
        logger.info("Created and sent response");

        return new ResponseEntity<>(reactionDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Integer> deleteReaction(@PathVariable String id) {
        logger.info("Deleting reaction with id: {}", id);
        Integer deleted = reactionService.deleteReaction(Long.parseLong(id));

        if (deleted != 0) {
            logger.info("Successfully deleted reaction with id: {}", id);
            return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        }
        logger.error("Failed to delete reaction with id: {}", id);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
