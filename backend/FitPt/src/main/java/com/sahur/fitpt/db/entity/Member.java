package com.sahur.fitpt.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Column(name = "member_birth")
    private LocalDateTime memberBirth;

    @Column(name = "member_height")
    private Float memberHeight;

    @Column(name = "member_weight")
    private Float memberWeight;

    @Column(name = "member_systolic")
    private Integer memberSystolic;

    @Column(name = "member_diastolic")
    private Integer memberDiastolic;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Column(name = "member_start_date")
    private LocalDateTime memberStartDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Column(name = "member_end_date")
    private LocalDateTime memberEndDate;
}