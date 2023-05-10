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
@Table(name = "banned")
public class Banned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "towards_user", nullable = false)
    private User towards;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group")
    private Group group;

    @Column
    private boolean deleted;
}
