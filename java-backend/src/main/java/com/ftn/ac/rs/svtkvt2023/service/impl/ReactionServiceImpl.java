package com.ftn.ac.rs.svtkvt2023.service.impl;

import com.ftn.ac.rs.svtkvt2023.indexrepository.GroupIndexRepository;
import com.ftn.ac.rs.svtkvt2023.indexrepository.PostIndexRepository;
import com.ftn.ac.rs.svtkvt2023.model.EnumReactionType;
import com.ftn.ac.rs.svtkvt2023.model.dto.ReactionDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.*;
import com.ftn.ac.rs.svtkvt2023.repository.ReactionRepository;
import com.ftn.ac.rs.svtkvt2023.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {

    private ReactionRepository reactionRepository;
    private GroupService groupService;
    private PostService postService;
    private CommentService commentService;
    private UserService userService;
    private GroupIndexRepository groupIndexRepository;
    private PostIndexRepository postIndexRepository;

    @Autowired
    public void setReactionRepository(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setGroupIndexRepository(GroupIndexRepository groupIndexRepository) {
        this.groupIndexRepository = groupIndexRepository;
    }

    @Autowired
    public void setPostIndexRepository(PostIndexRepository postIndexRepository) {
        this.postIndexRepository = postIndexRepository;
    }

    private static final Logger logger = LogManager.getLogger(ReactionServiceImpl.class);

    @Override
    public Reaction findById(Long id) {
        Optional<Reaction> reaction = reactionRepository.findById(id);
        if (reaction.isPresent())
            return reaction.get();
        logger.error("Repository search for reaction with id: {} returned null", id);
        return null;
    }

    @Override
    public List<Reaction> findReactionsForPost(Long postId) {
        Optional<List<Reaction>> reactions = reactionRepository.findAllByOnPostId(postId);
        if (reactions.isPresent())
            return reactions.get();
        logger.error("Repository search for reactions for post with id: {} returned null", postId);
        return null;
    }

    @Override
    public List<Reaction> findReactionsForComment(Long commentId) {
        Optional<List<Reaction>> reactions = reactionRepository.findAllByOnCommentId(commentId);
        if (reactions.isPresent())
            return reactions.get();
        logger.error("Repository search for reactions for comment with id: {} returned null", commentId);
        return null;
    }

    @Override
    public Reaction createReaction(ReactionDTO reactionDTO) {
        Optional<Reaction> reaction = reactionRepository.findById(reactionDTO.getId());

        if (reaction.isPresent()) {
            logger.error("Reaction with id: {} already exists in repository", reactionDTO.getId());
            return null;
        }

        Reaction newReaction = new Reaction();
        newReaction.setType(EnumReactionType.valueOf(reactionDTO.getReactionType()));
        newReaction.setTimestamp(LocalDate.parse(reactionDTO.getTimestamp()));

        User user = userService.findById(reactionDTO.getMadeByUserId());

        if (user == null) {
            logger.error("User with id: {}, that made the reaction, was not found in the database",
                    reactionDTO.getMadeByUserId());
            return null;
        }

        newReaction.setMadeBy(user);

        if (reactionDTO.getOnCommentId() != null) {
            Comment comment = commentService.findById(reactionDTO.getOnCommentId());
            newReaction.setOnComment(comment);

            EnumReactionType reactionType = EnumReactionType.valueOf(reactionDTO.getReactionType());
            this.updatePostIndexAfterReaction(comment.getBelongsToPost().getId(), reactionType);
            this.updateGroupIndexAfterReactionOrDelete(comment.getBelongsToPost().getId());
        }

        if (reactionDTO.getOnPostId() != null) {
            Post post = postService.findById(reactionDTO.getOnPostId());
            newReaction.setOnPost(post);

            EnumReactionType reactionType = EnumReactionType.valueOf(reactionDTO.getReactionType());
            this.updatePostIndexAfterReaction(reactionDTO.getOnPostId(), reactionType);
            this.updateGroupIndexAfterReactionOrDelete(reactionDTO.getOnPostId());
        }

        newReaction.setDeleted(false);
        newReaction = reactionRepository.save(newReaction);

        return newReaction;
    }

    @Override
    public Integer deleteReaction(Long id) {
        Reaction deletedReaction = reactionRepository.findById(id).orElse(null);
        if (deletedReaction != null) {
            EnumReactionType reactionType = deletedReaction.getType();
            this.updatePostIndexAfterDelete(deletedReaction.getOnPost().getId(), reactionType);
            this.updateGroupIndexAfterReactionOrDelete(deletedReaction.getOnPost().getId());
        }
        return reactionRepository.deleteReactionById(id);
    }

    @Override
    public Integer deletePostReactions(Long postId) {
        //objava ne postoji u trenutku izvrsenja metode
        //azurira se samo index grupe
        this.updateGroupIndexAfterReactionOrDelete(postId);
        return reactionRepository.deleteReactionsByOnPostId(postId);
    }

    @Override
    public Integer deleteCommentReactions(Long commentId) {
        Comment deletedComment = commentService.findById(commentId);
        this.findReactionsForComment(commentId).forEach(reaction -> {
            EnumReactionType reactionType = reaction.getType();
            this.updatePostIndexAfterDelete(deletedComment.getBelongsToPost().getId(), reactionType);
            this.updateGroupIndexAfterReactionOrDelete(deletedComment.getBelongsToPost().getId());
        });
        return reactionRepository.deleteReactionsByOnCommentId(commentId);
    }

    @Override
    public Integer deleteReactionsFromUser(Long userId) {
        return reactionRepository.deleteReactionsMadeByUserId(userId);
    }

    private void updatePostIndexAfterReaction(Long postId, EnumReactionType reactionType) {
        postIndexRepository
                .findByDatabaseId(postId)
                .ifPresent(postIndex -> {
                    int reactionValue = 0;
                    switch (reactionType) {
                        case LIKE -> reactionValue = 1;
                        case DISLIKE -> reactionValue = -1;
                        case HEART -> reactionValue = 5;
                    }
                    postIndex.setNumberOfLikes(postIndex.getNumberOfLikes() + reactionValue);
                    postIndexRepository.save(postIndex);
                });
    }

    private void updatePostIndexAfterDelete(Long postId, EnumReactionType reactionType) {
        postIndexRepository
                .findByDatabaseId(postId)
                .ifPresent(postIndex -> {
                    int reactionValue = 0;
                    switch (reactionType) {
                        case LIKE -> reactionValue = -1;
                        case DISLIKE -> reactionValue = 1;
                        case HEART -> reactionValue = -5;
                    }
                    postIndex.setNumberOfLikes(postIndex.getNumberOfLikes() + reactionValue);
                    postIndexRepository.save(postIndex);
                });
    }

    private void updateGroupIndexAfterReactionOrDelete(Long postId) {
        Group group = groupService.checkIfPostInGroup(postId);
        if (group != null) {
            final Long[] numOfLikes = {0L};

            groupService.findPostsByGroupId(group.getId()).forEach(pId -> {
                postIndexRepository.findByDatabaseId(pId).ifPresent(postIndex -> {
                    numOfLikes[0] += postIndex.getNumberOfLikes();
                });
            });

            groupIndexRepository
                    .findByDatabaseId(group.getId())
                    .ifPresent(groupIndex -> {
                        double avgLikes = (double) numOfLikes[0] / groupIndex.getNumberOfPosts();
                        groupIndex.setAverageLikes(avgLikes);
                        groupIndexRepository.save(groupIndex);
                    });
        }
    }
}
