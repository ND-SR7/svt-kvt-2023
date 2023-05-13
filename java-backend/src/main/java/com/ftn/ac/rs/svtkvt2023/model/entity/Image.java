package com.ftn.ac.rs.svtkvt2023.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belongs_to_post_id", referencedColumnName = "id")
    private Post belongsToPost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belongs_to_user_id", referencedColumnName = "id")
    private User belongsToUser;

    @Column(nullable = false)
    private boolean deleted;
}
