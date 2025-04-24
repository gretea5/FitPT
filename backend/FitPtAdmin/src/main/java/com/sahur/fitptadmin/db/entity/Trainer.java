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
@Table(name = "trainer")
@Entity
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @Size(max = 255)
    @NotNull
    @Column(name = "trainer_name", nullable = false)
    private String trainerName;

    @Size(max = 255)
    @NotNull
    @Column(name = "trainer_login_id", nullable = false)
    private String trainerLoginId;

    @Size(max = 255)
    @NotNull
    @Column(name = "trainer_pw", nullable = false)
    private String trainerPw;

    @OneToMany(mappedBy = "users")
    private List<Users> users;

}
