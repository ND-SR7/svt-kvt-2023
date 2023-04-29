package com.ftn.ac.rs.svtkvt2023.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "group_requests")
public class GroupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column //ako je null, nije pregledan
    private Boolean approved;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column //ako je null, nije pregledan
    private LocalDateTime at;

    //TODO veze izmedju entiteta
}
