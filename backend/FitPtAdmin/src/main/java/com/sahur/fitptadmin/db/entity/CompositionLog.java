package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "composition_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompositionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "composition_log_id")
    private Long compositionLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "protein")
    private Float protein;

    @Column(name = "bmr")
    private Float bmr;

    @Column(name = "mineral")
    private Float mineral;

    @Column(name = "body_age")
    private Integer bodyAge;

    @Column(name = "smm")
    private Float smm;

    @Column(name = "icw")
    private Float icw;

    @Column(name = "ecw")
    private Float ecw;

    @Column(name = "bfm")
    private Float bfm;

    @Column(name = "bfp")
    private Float bfp;

    @Column(name = "weight")
    private Float weight;
}
