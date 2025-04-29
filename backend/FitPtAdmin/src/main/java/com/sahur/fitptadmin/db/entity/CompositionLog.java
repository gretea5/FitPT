package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "composition_log")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompositionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "composition_log_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Column(name = "created_at")
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

}
