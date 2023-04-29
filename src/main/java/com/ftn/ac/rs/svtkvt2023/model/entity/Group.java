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
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String creationDate;

    @Column(nullable = false)
    private Boolean isSuspended;

    @Column //ako je null, nije suspendovana
    private String suspendedReason;

    //TODO veze izmedju entiteta
}
