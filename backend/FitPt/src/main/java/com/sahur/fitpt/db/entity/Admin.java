package com.sahur.fitpt.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "admin_login_id", nullable = false)
    private String adminLoginId;

    @Column(name = "admin_pw", nullable = false)
    private String adminPw;

    @Column(name = "gym_name")
    private String gymName;

    @Column(name = "gym_addr")
    private String gymAddr;

    @OneToMany(mappedBy = "admin")
    private List<Trainer> trainers = new ArrayList<>();

    @OneToMany(mappedBy = "admin")
    private List<Member> members = new ArrayList<>();
}