package com.ftn.ac.rs.svtkvt2023.service;

import com.ftn.ac.rs.svtkvt2023.model.dto.ReactionDTO;
import com.ftn.ac.rs.svtkvt2023.model.entity.Reaction;

import java.util.List;

public interface ReactionService {

    Reaction findById(Long id);

    List<Reaction> findReactionsForPost(Long postId);

    List<Reaction> findReactionsForComment(Long commentId);

    Reaction createReaction(ReactionDTO reactionDTO);

    Integer deleteReaction(Long id);

    Integer deletePostReactions(Long postId);

    Integer deleteCommentReactions(Long commentId);

    Integer deleteReactionsFromUser(Long userId);
}
