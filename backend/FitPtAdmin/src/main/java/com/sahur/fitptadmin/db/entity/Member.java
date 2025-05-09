package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "member_gender")
    private String memberGender;

    // @CreationTimestamp
    @Column(name = "member_birth")
    private LocalDateTime memberBirth;

    @Column(name = "member_height")
    private Float memberHeight;

    @Column(name = "member_weight")
    private Float memberWeight;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

    @OneToMany(mappedBy = "member")
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<CompositionLog> compositionLogs = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<FcmToken> fcmTokens = new ArrayList<>();

    public void update(String memberName, String memberGender, LocalDateTime memberBirth,
                       Float memberHeight, Float memberWeight, Trainer trainer, Admin admin) {
        this.memberName = memberName;
        this.memberGender = memberGender;
        this.memberBirth = memberBirth;
        this.memberHeight = memberHeight;
        this.memberWeight = memberWeight;
        this.trainer = trainer;
        this.admin = admin;
    }

    public void updatePartial(String memberName, Float memberWeight) {
        if (memberName != null) this.memberName = memberName;
        if (memberWeight != null) this.memberWeight = memberWeight;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateTrainer(Trainer trainer) {
        this.trainer = trainer;
    }


}