package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "gym")
@Entity
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gym_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "gym_name", nullable = false)
    private String gymName;

    @Size(max = 255)
    @NotNull
    @Column(name = "gym_addr", nullable = false)
    private String gymAddr;

    @OneToMany(mappedBy = "users")
    private List<Users> users;

    @OneToMany(mappedBy = "trainer")
    private List<Trainer> trainer;

}
