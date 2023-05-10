package com.ftn.ac.rs.svtkvt2023.model.entity;

import com.ftn.ac.rs.svtkvt2023.model.EnumReportReason;
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
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    @Column(nullable = false)
    private EnumReportReason reason;

    @Column(nullable = false)
    private LocalDate timestamp;

    @OneToOne
    @Column(nullable = false)
    private User byUser;

    @Column //ako je null, nije pregledan
    private Boolean accepted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "on_user")
    private User onUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "on_post")
    private Post onPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "on_comment")
    private Comment onComment;

    @Column(nullable = false)
    private boolean deleted;
}
