package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.model.EnumReactionType;
import com.ftn.ac.rs.svtkvt2023.model.dto.ReactionDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Comment;
import com.ftn.ac.rs.svtkvt2023.model.entity.Post;
import com.ftn.ac.rs.svtkvt2023.model.entity.Reaction;
import com.ftn.ac.rs.svtkvt2023.model.entity.User;
import com.ftn.ac.rs.svtkvt2023.repository.ReactionRepository;
import com.ftn.ac.rs.svtkvt2023.service.CommentService;
import com.ftn.ac.rs.svtkvt2023.service.PostService;
import com.ftn.ac.rs.svtkvt2023.service.ReactionService;
import com.ftn.ac.rs.svtkvt2023.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {

    private ReactionRepository reactionRepository;

    @Autowired
    public void setReactionRepository(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    private PostService postService;

    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    private CommentService commentService;

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Reaction findById(Long id) {
        Optional<Reaction> reaction = reactionRepository.findById(id);
        if (!reaction.isEmpty())
            return reaction.get();
        return null;
    }

    @Override
    public List<Reaction> findReactionsForPost(Long postId) {
        Optional<List<Reaction>> reactions = reactionRepository.findAllByOnPostId(postId);
        if (!reactions.isEmpty())
            return reactions.get();
        return null;
    }

    @Override
    public List<Reaction> findReactionsForComment(Long commentId) {
        Optional<List<Reaction>> reactions = reactionRepository.findAllByOnCommentId(commentId);
        if (!reactions.isEmpty())
            return reactions.get();
        return null;
    }

    @Override
    public Reaction createReaction(ReactionDTO reactionDTO) {
        Optional<Reaction> reaction = reactionRepository.findById(reactionDTO.getId());

        if (reaction.isPresent())
            return null;

        Reaction newReaction = new Reaction();
        newReaction.setType(EnumReactionType.valueOf(reactionDTO.getReactionType()));
        newReaction.setTimestamp(LocalDate.parse(reactionDTO.getTimestamp()));

        User user = userService.findById(reactionDTO.getMadeByUserId());

        if (user == null)
            return null;

        newReaction.setMadeBy(user);

        if (reactionDTO.getOnCommentId() != null) {
            Comment comment = commentService.findById(reactionDTO.getOnCommentId());
            newReaction.setOnComment(comment);
        }

        if (reactionDTO.getOnPostId() != null) {
            Post post = postService.findById(reactionDTO.getOnPostId());
            newReaction.setOnPost(post);
        }

        newReaction.setDeleted(false);
        newReaction = reactionRepository.save(newReaction);

        return newReaction;
    }

    @Override
    public Integer deleteReaction(Long id) {
        return reactionRepository.deleteReactionById(id);
    }

    @Override
    public Integer deletePostReactions(Long postId) {
        return reactionRepository.deleteReactionsByOnPostId(postId);
    }

    @Override
    public Integer deleteCommentReactions(Long commentId) {
        return reactionRepository.deleteReactionsByOnCommentId(commentId);
    }

    @Override
    public Integer deleteReactionsFromUser(Long userId) {
        return reactionRepository.deleteReactionsMadeByUserId(userId);
    }
}
