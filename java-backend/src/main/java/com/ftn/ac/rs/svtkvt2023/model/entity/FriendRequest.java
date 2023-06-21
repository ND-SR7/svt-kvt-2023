package com.ftn.ac.rs.svtkvt2023.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friend_requests")
@SQLDelete(sql = "update friend_requests set deleted = true where id=?")
@Where(clause = "deleted = false")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column //ako je null, znaci da nije odgovoreno
    private Boolean approved;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column //ako je null, znaci da nije odgovoreno
    private LocalDateTime at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", referencedColumnName = "id", nullable = false)
    private User from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", referencedColumnName = "id", nullable = false)
    private User to;

    @Column
    private boolean deleted;
}
