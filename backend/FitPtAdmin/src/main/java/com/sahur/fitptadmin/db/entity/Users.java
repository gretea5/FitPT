package com.sahur.fitptadmin.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "users")
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @Size(max = 255)
    @Column(name = "user_name")
    private String userName;

    @Size(max = 30)
    @Column(name = "user_gender", length = 30)
    private String userGender;

    @Size(max = 20)
    @Column(name = "user_birth", length = 20)
    private String userBirth;

    @Column(name = "user_height")
    private Float userHeight;

    @Column(name = "user_weight")
    private Float userWeight;

    @Column(name = "user_systolic")
    private Integer userSystolic;

    @Column(name = "user_diastolic")
    private Integer userDiastolic;

    @OneToMany(mappedBy = "users")
    private List<CompositionLog> compositionLogs = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<HrLog> hrLogs = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<StressLog> stressLogs = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<TemperatureLog> temperatureLogs = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<Notifications> notifications = new ArrayList<>();

}
