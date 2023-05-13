package com.ftn.ac.rs.svtkvt2023.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String text;

    @Column(nullable = false)
    private LocalDate timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replies_to_comment_id", referencedColumnName = "id")
    private Comment repliesTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belongs_to_user_id", referencedColumnName = "id", nullable = false)
    private User belongsToUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belongs_to_post_id", referencedColumnName = "id")
    private Post belongsToPost;

    @Column(nullable = false)
    private boolean deleted;
}
