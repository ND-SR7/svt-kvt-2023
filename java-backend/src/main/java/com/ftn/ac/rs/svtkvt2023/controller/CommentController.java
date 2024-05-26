package com.ftn.ac.rs.svtkvt2023.controller;

import com.ftn.ac.rs.svtkvt2023.model.dto.CommentDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Comment;
import com.ftn.ac.rs.svtkvt2023.service.CommentService;
import com.ftn.ac.rs.svtkvt2023.service.ReactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    CommentService commentService;
    ReactionService reactionService;

    private static final Logger logger = LogManager.getLogger(CommentController.class);

    @Autowired
    public CommentController(CommentService commentService, ReactionService reactionService) {
        this.commentService = commentService;
        this.reactionService = reactionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getOne(@PathVariable String id) {
        logger.info("Finding comment for id: {}", id);
        Comment comment = commentService.findById(Long.parseLong(id));

        if (comment == null) {
            logger.error("Comment not found for id: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CommentDTO commentDTO = new CommentDTO(comment);

        logger.info("Found comment for id: {}", id);
        return new ResponseEntity<>(commentDTO, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CommentDTO> addComment(@RequestBody @Validated CommentDTO newComment) {
        logger.info("Creating comment from DTO");
        Comment createdComment = commentService.createComment(newComment);

        if (createdComment == null) {
            logger.error("Comment couldn't be created from DTO");
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        logger.info("Creating response with new comment");
        CommentDTO commentDTO = new CommentDTO(createdComment);
        logger.info("Created and sent response with new comment");

        return new ResponseEntity<>(commentDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<CommentDTO> editComment(@PathVariable String id,
                                            @RequestBody @Validated CommentDTO editedComment) {
        logger.info("Finding original comment for id: {}", editedComment.getId());
        Comment oldComment = commentService.findById(Long.parseLong(id));

        if (oldComment == null) {
            logger.error("Original comment not found for id: {}", editedComment.getId());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        logger.info("Applying changes to comment");
        oldComment.setText(editedComment.getText());
        oldComment.setTimestamp(LocalDate.parse(editedComment.getTimestamp()));

        oldComment = commentService.updateComment(oldComment);

        logger.info("Creating response with edited comment");
        CommentDTO updatedComment = new CommentDTO(oldComment);
        logger.info("Created and sent response with edited comment");

        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Integer> deleteComment(@PathVariable String id) {
        logger.info("Deleting comment with id: {}", id);
        commentService.deleteCommentReply(Long.parseLong(id));
        Integer deleted = commentService.deleteComment(Long.parseLong(id));
        reactionService.deleteCommentReactions(Long.parseLong(id));

        if (deleted != 0) {
            logger.info("Successfully deleted comment with id: {}", id);
            return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        }
        logger.error("Failed to delete comment with id: {}", id);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
