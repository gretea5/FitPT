package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Long trainerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "trainer_name", nullable = false)
    private String trainerName;

    @Column(name = "trainer_login_id", nullable = false)
    private String trainerLoginId;

    @Column(name = "trainer_pw", nullable = false)
    private String trainerPw;

    @OneToMany(mappedBy = "trainer")
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "trainer")
    private List<Report> reports = new ArrayList<>();

    public void updateTrainerInfo(String trainerName, String trainerLoginId, String trainerPw) {
        this.trainerName = trainerName;
        this.trainerLoginId = trainerLoginId;
        this.trainerPw = trainerPw;
    }

}
