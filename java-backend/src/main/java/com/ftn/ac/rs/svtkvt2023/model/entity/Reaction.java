package com.ftn.ac.rs.svtkvt2023.model.entity;

import com.ftn.ac.rs.svtkvt2023.model.EnumReactionType;
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
@Table(name = "reactions")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumReactionType type;

    @Column(nullable = false)
    private LocalDate timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "made_by_user_id", referencedColumnName = "id", nullable = false)
    private User madeBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "on_comment_id", referencedColumnName = "id")
    private Comment onComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "on_post_id", referencedColumnName = "id")
    private Post onPost;

    @Column(nullable = false)
    private boolean deleted;
}
