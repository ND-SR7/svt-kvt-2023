package com.ftn.ac.rs.svtkvt2023.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groupss") //2 's' zbog kolizije sa rezervisanom recju
@SQLDelete(sql = "update groupss set deleted = true where id=?")
@Where(clause = "deleted = false")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80, unique = true)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "is_suspended", nullable = false)
    private boolean suspended;

    @Column(name = "suspended_reason") //ako je null, nije suspendovana
    private String suspendedReason;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "group_posts", inverseJoinColumns=@JoinColumn(name="post_id"))
    private List<Post> posts;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "group_admins", inverseJoinColumns=@JoinColumn(name="admin_id"))
    private List<User> groupAdmins;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "group_members", inverseJoinColumns=@JoinColumn(name="member_id"))
    private List<User> groupMembers;

    @Column(nullable = false)
    private boolean deleted;
}
