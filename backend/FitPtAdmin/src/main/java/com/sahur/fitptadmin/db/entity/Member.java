package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "member_gender")
    private String memberGender;

    @CreationTimestamp
    @Column(name = "member_birth")
    private LocalDateTime memberBirth;

    @Column(name = "member_height")
    private Float memberHeight;

    @Column(name = "member_weight")
    private Float memberWeight;

    @OneToMany(mappedBy = "member")
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<CompositionLog> compositionLogs = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<FcmToken> fcmTokens = new ArrayList<>();
}